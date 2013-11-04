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

import java.util.Date;
import java.util.List;

import org.alfresco.repo.content.ContentContext;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.repo.content.ContentStore.ContentUrlHandler;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * A Content Store which compresses certain mimetypes
 *  transparently when writing/reading from a real
 *  {@link ContentStore}
 */
public class CompressingContentStore implements ContentStore, InitializingBean
{
   private static Log logger = LogFactory.getLog(CompressingContentStore.class);
   
   /** The real ContentStore to read/write from */
   private ContentStore realContentStore;
   /** Which mimetypes should be compressed */
   private List<String> compressMimeTypes;
   /** Which compression type to use */
   private String compressionType;
   
   @Override
   public void afterPropertiesSet() throws Exception
   {
      // Compression type is optional, use GZip if in doubt
      if (compressionType == null || compressionType.isEmpty())
      {
         compressionType = CompressorStreamFactory.GZIP;
      }
      
      // Real Content Store and MimeTypes must be given
      if (compressMimeTypes == null || compressMimeTypes.isEmpty())
      {
         throw new IllegalArgumentException("'compressMimeTypes' must be given");
      }
      if (realContentStore == null)
      {
         throw new IllegalArgumentException("'realContentStore' must be given");
      }
   }

   @Override
   public ContentReader getReader(String contentUrl)
   {
      ContentReader reader = realContentStore.getReader(contentUrl);
      if (shouldCompress(reader))
      {
         return decompressIfRequired(reader);
      }
      
      // Use the content reader onto the main store
      return reader;
   }

   @Override
   @SuppressWarnings("deprecation")
   public ContentWriter getWriter(ContentReader existingContentReader, String newContentUrl)
   {
      ContentWriter realWriter = realContentStore.getWriter(existingContentReader, newContentUrl);
      if (shouldCompress(existingContentReader))
      {
         // Should be compressed
         return new CompressingContentWriter(realWriter);
      }
      else
      {
         // Pass through unchanged to the real store
         return realWriter;
      }
   }
   
   @Override
   public ContentWriter getWriter(ContentContext context)
   {
      ContentReader existingContentReader = context.getExistingContentReader();
      ContentWriter realWriter = realContentStore.getWriter(context);
      if (shouldCompress(existingContentReader))
      {
         // Should be compressed
         return new CompressingContentWriter(realWriter);
      }
      else
      {
         // Pass through unchanged to the real store
         return realWriter;
      }
   }
   
   /**
    * Should the content be compressed?
    * This will be based on the Mime Type set on the reader
    */
   protected boolean shouldCompress(ContentReader reader)
   {
      if (reader.getMimetype() != null && compressMimeTypes.contains(reader.getMimetype()))
      {
         return true;
      }
      return false;
   }

   /**
    * Checks for the compression header, and returns a decompressed
    *  version if present.
    * (Because content could have been stored before this module was
    *  applied, some existing content could be uncompressed)
    */
   protected ContentReader decompressIfRequired(ContentReader reader)
   {
      // We need the first few hundred bytes to detect with
      
   }

   
   // These just delegate to the real ContentStore

   @Override
   public void getUrls(ContentUrlHandler handler) 
         throws ContentIOException
   {
      realContentStore.getUrls(handler);
   }
   @Override
   public void getUrls(Date from, Date to, ContentUrlHandler handler)
         throws ContentIOException
   {
      realContentStore.getUrls(from, to, handler);
   }
   @Override
   public boolean delete(String contentUrl)
   {
      return realContentStore.delete(contentUrl);
   }
   
   @Override
   public boolean exists(String contentUrl)
   {
      return realContentStore.exists(contentUrl);
   }
   @Override
   public String getRootLocation()
   {
      return realContentStore.getRootLocation();
   }
   @Override
   public long getSpaceFree()
   {
      return realContentStore.getSpaceFree();
   }
   @Override
   public long getSpaceTotal()
   {
      return realContentStore.getSpaceTotal();
   }
   @Override
   @SuppressWarnings("deprecation")
   public long getSpaceUsed()
   {
      return realContentStore.getSpaceUsed();
   }
   @Override
   @SuppressWarnings("deprecation")
   public long getTotalSize()
   {
      return realContentStore.getTotalSize();
   }
   @Override
   public boolean isContentUrlSupported(String contentUrl)
   {
      return realContentStore.isContentUrlSupported(contentUrl);
   }
   @Override
   public boolean isWriteSupported()
   {
      return realContentStore.isWriteSupported();
   }
   
   public void setRealContentStore(ContentStore realContentStore)
   {
      this.realContentStore = realContentStore;
   }
   public void setCompressMimeTypes(List<String> compressMimeTypes)
   {
      this.compressMimeTypes = compressMimeTypes;
   }
   public void setCompressionType(String compressionType)
   {
      this.compressionType = compressionType;
   }
}
