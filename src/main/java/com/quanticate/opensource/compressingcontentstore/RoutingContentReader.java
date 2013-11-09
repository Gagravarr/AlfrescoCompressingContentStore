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

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Locale;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.content.AbstractContentReader;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
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
   private DecompressingContentReader decompressingContentReader;
   
   private Boolean shouldDecompress = null;

   public RoutingContentReader(ContentReader realContentReader, CompressingContentStore contentStore)
   {
      super(realContentReader.getContentUrl());
      this.realContentReader = realContentReader;
      this.contentStore = contentStore;
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
         return realContentReader.getReader();
      }
      
      // Otherwise give a new one of ourselves, to
      //  allow for a later decision
      return new RoutingContentReader(realContentReader.getReader(), contentStore);
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
         return realContentReader.getReadableChannel();
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
   public void setEncoding(String encoding)
   {
      super.setEncoding(encoding);
      realContentReader.setEncoding(encoding);
   }
   @Override
   public void setLocale(Locale locale)
   {
      super.setLocale(locale);
      realContentReader.setLocale(locale);
   }
   @Override
   public void setMimetype(String mimetype)
   {
      super.setMimetype(mimetype);
      realContentReader.setMimetype(mimetype);
   }
}
