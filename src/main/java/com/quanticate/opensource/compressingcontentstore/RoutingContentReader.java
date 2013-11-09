/* ====================================================================
  Copyright 2013 Quanticate Ltd

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
==================================================================== */
package com.quanticate.opensource.compressingcontentstore;

import java.nio.channels.ReadableByteChannel;

import org.alfresco.repo.content.AbstractContentReader;
import org.alfresco.repo.content.ContentHelper;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A wrapper around a pair of Content Readers, which will
 *  apply some operations to both, and pick which one to
 *  use for the rest based on the mimetype.
 * This works around the fact that at the time you ask the
 *  {@link ContentStore} for a {@link ContentReader}, you
 *  haven't yet supplied the information on the mimetype!
 */
public class RoutingContentReader extends AbstractContentReader
{
   private static Log logger = LogFactory.getLog(RoutingContentReader.class);

   private final CompressingContentStore contentStore;
   private final ContentReader realContentReader;
   private final AbstractContentReader realContentReaderA;
   private DecompressingContentReader decompressingContentReader;
   
   private Boolean shouldDecompress = null;

   public RoutingContentReader(ContentReader realContentReader, CompressingContentStore contentStore)
   {
      super(realContentReader.getContentUrl());
      this.realContentReader = realContentReader;
      this.contentStore = contentStore;
      
      // We can shortcut some things if we're wrapping an AbstractContentReader
      if (realContentReader instanceof AbstractContentReader)
      {
         this.realContentReaderA = (AbstractContentReader)realContentReader;
      }
      else
      {
         this.realContentReaderA = null;
      }
   }
   
   protected ContentReader createRawReader() throws ContentIOException
   {
      if (realContentReaderA != null)
      {
         // Use a createReader call
         return ContentHelper.callCreateReader(realContentReaderA);
      }
      else
      {
         // A getReader call will have to do
         return realContentReader.getReader();
      }
   }

   @Override
   protected ContentReader createReader() throws ContentIOException
   {
      // If we know which one, always return that
      if (shouldDecompress == Boolean.TRUE)
      {
         return decompressingContentReader.createReader();
      }
      if (shouldDecompress == Boolean.FALSE)
      {
         return createRawReader();
      }
      
      // Otherwise give a new one of ourselves, to
      //  allow for a later decision
      return new RoutingContentReader(createRawReader(), contentStore);
   }

   @Override
   protected ReadableByteChannel getDirectReadableChannel()
         throws ContentIOException
   {
      // If we know which one to use, go to that reader
      if (shouldDecompress == Boolean.TRUE)
      {
         return decompressingContentReader.getDirectReadableChannel();
      }
      if (shouldDecompress == Boolean.FALSE)
      {
         if (realContentReaderA != null)
         {
            return ContentHelper.callGetDirectReadableChannel(realContentReaderA);
         }
         else
         {
            return realContentReader.getReadableChannel();
         }
      }

      // Oh dear, we don't know the mimetype!
      // Log, then hope they it's something without one
      logger.warn("No MimeType specified for " + getContentUrl() + " - assuming non-compressed");
      return realContentReader.getReadableChannel();
   }
   
   @Override
   public boolean exists()
   {
      return realContentReader.exists();
   }
   @Override
   public long getLastModified()
   {
      return realContentReader.getLastModified();
   }
   
   @Override
   public long getSize()
   {
      return -1L;
   }

   @Override
   public void setMimetype(String mimetype)
   {
      super.setMimetype(mimetype);
      
      // Now decide on DeCompressing vs Normal
      if (contentStore.shouldCompress(mimetype))
      {
         // We need to decompress!
         shouldDecompress = Boolean.TRUE;
         
         // Create the wrapping decompressing reader
         decompressingContentReader = new DecompressingContentReader(realContentReader);
         decompressingContentReader.setMimetype(mimetype);
         decompressingContentReader.setEncoding(getEncoding());
         decompressingContentReader.setLocale(getLocale());
      }
      else
      {
         // We're not compressing
         shouldDecompress = Boolean.FALSE;
      }
   }
}
