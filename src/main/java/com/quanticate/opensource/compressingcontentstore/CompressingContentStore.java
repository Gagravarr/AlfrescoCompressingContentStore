package com.quanticate.opensource.compressingcontentstore;

import java.util.List;

import org.alfresco.repo.content.ContentStore;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Content Store which compresses certain mimetypes
 *  transparently when writing/reading from a real
 *  {@link ContentStore}
 */
public class CompressingContentStore implements ContentStore 
{
   private static Log logger = LogFactory.getLog(CompressingContentStore.class);
   
   /** The real ContentStore to read/write from */
   private ContentStore realContentStore;
   /** Which mimetypes should be compressed */
   private List<String> compressMimeTypes;
   
   @Override
   public ContentReader getReader(String contentUrl)
   {
      // TODO Implement
      return null;
   }

   @Override
   public ContentWriter getWriter(ContentReader existingContentReader, String newContentUrl)
   {
      // TODO Implement
      return null;
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
      // TODO Auto-generated method stub
      return null;
   }
   @Override
   public long getSpaceFree()
   {
      // TODO Auto-generated method stub
      return 0;
   }
   @Override
   public long getSpaceTotal()
   {
      // TODO Auto-generated method stub
      return 0;
   }
   @Override
   public long getSpaceUsed()
   {
      // TODO Auto-generated method stub
      return 0;
   }
   @Override
   public long getTotalSize()
   {
      // TODO Auto-generated method stub
      return 0;
   }
   @Override
   public boolean isContentUrlSupported(String arg0)
   {
      // TODO Auto-generated method stub
      return false;
   }
   @Override
   public boolean isWriteSupported()
   {
      // TODO Auto-generated method stub
      return false;
   }
   
   public void setRealContentStore(ContentStore realContentStore)
   {
      this.realContentStore = realContentStore;
   }
   public void setCompressMimeTypes(List<String> compressMimeTypes)
   {
      this.compressMimeTypes = compressMimeTypes;
   }
}