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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Locale;

import org.alfresco.repo.content.AbstractContentReader;
import org.alfresco.repo.content.ContentHelper;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Content Reader which decompresses data as it reads
 *  from the real Reader, if it's compressed
 */
public class DecompressingContentReader extends AbstractContentReader
{
   private static final Log logger = LogFactory.getLog(DecompressingContentReader.class);

   private final ContentReader realContentReader;
   private final AbstractContentReader realContentReaderA;

   public DecompressingContentReader(ContentReader realContentReader)
   {
      super(realContentReader.getContentUrl());
      this.realContentReader = realContentReader;
      
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

   @Override
   protected ContentReader createReader() throws ContentIOException
   {
      ContentReader toWrap = null;
      
      // If it's an AbstractContentReader, call createReader on that
      if (realContentReaderA != null)
      {
         toWrap = ContentHelper.callCreateReader(realContentReaderA);
      }
      else
      {
         toWrap = realContentReader.getReader();
      }
      
      // Wrap the new reader with ourselves
      return new DecompressingContentReader(toWrap);
   }
   
   protected ReadableByteChannel getRawChannel()
   {
      ReadableByteChannel rawChannel = null;
      if (realContentReaderA != null)
      {
         rawChannel = ContentHelper.callGetDirectReadableChannel(realContentReaderA);
      }
      else
      {
         rawChannel = realContentReader.getReadableChannel();
      }
      return rawChannel;
   }

   @Override
   protected ReadableByteChannel getDirectReadableChannel()
         throws ContentIOException
   {
      // Get a Channel onto the real data
      ReadableByteChannel rawChannel = getRawChannel();
      
      // Wrap this as an InputStream - Commons Compress is Stream not Channel based
      // Note that Commons Compress needs to mark/reset a bit to identify
      InputStream rawInp = new BufferedInputStream(Channels.newInputStream(rawChannel), 32);
      
      // Try to process it as a compressed stream
      try
      {
         CompressorInputStream decompressed = new CompressorStreamFactory()
                .createCompressorInputStream(rawInp);
         logger.debug("Detected compressed data as " + decompressed.getClass().getName());
         return Channels.newChannel(decompressed);
      }
      catch (CompressorException e)
      {
         logger.info("Unable to decompress " + realContentReader, e);
      }
      
      // Tidy up that channel, and re-fetch the real one
      try
      {
         rawInp.close();
         rawChannel.close();
      }
      catch (IOException e) 
      {
         logger.warn("Error tidying up", e);
      }
      
      logger.debug("Using raw form for " + getContentUrl());
      return getRawChannel();
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
