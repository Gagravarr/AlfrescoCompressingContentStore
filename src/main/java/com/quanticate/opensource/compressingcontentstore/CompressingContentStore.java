package com.quanticate.opensource.compressingcontentstore;

import java.util.Date;
import java.util.List;

import org.alfresco.repo.content.ContentContext;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.repo.content.ContentStore.ContentUrlHandler;
import org.alfresco.service.cmr.repository.ContentIOException;
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
   public ContentWriter getWriter(ContentContext arg0)
   {
      // TODO Implement
      return null;
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
}