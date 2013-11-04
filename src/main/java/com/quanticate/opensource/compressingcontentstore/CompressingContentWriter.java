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

import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.repo.content.AbstractContentWriter;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Content Writer which compresses data as it writes
 *  into the real Writer
 */
public class CompressingContentWriter extends AbstractContentWriter 
{
   private static Log logger = LogFactory.getLog(CompressingContentWriter.class);
   
   private ContentWriter realContentWriter;
   private String compressionType;
   
   public CompressingContentWriter(ContentWriter realContentWriter, 
         ContentReader existingContentReader, String compressionType)
   {
      super(realContentWriter.getContentUrl(), existingContentReader);
      this.realContentWriter = realContentWriter;
      this.compressionType = compressionType;
   }

   public String getCompressionType()
   {
      return compressionType;
   }

   @Override
   public long getSize()
   {
      return -1L;
   }

   @Override
   protected ContentReader createReader() throws ContentIOException
   {
      // TODO
      return null;
   }

   @Override
   protected WritableByteChannel getDirectWritableChannel()
         throws ContentIOException
   {
      // Get the raw writer onto the real stream
      OutputStream rawOut = realContentWriter.getContentOutputStream();
      
      try {
         // Wrap that with the requested compression
         CompressorOutputStream compOut = new CompressorStreamFactory()
              .createCompressorOutputStream(compressionType, rawOut);

         logger.info("Compressing " + realContentWriter.getContentUrl() + " with " + compressionType);

         // Turn that into a channel and return
         return Channels.newChannel(compOut);
      }
      catch (CompressorException e)
      {
         throw new AlfrescoRuntimeException("Error compressing", e);
      }
   }
}
