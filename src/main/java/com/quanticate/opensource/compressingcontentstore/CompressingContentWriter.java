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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.alfresco.repo.content.ContentContext;
import org.alfresco.repo.content.ContentStore;
import org.alfresco.repo.content.ContentStore.ContentUrlHandler;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentStreamListener;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A Content Store which compresses certain mimetypes
 *  transparently when writing/reading from a real
 *  {@link ContentStore}
 */
public class CompressingContentWriter implements ContentWriter 
{
   private static Log logger = LogFactory.getLog(CompressingContentWriter.class);
   
   private ContentWriter realContentWriter;
   private String compressionType;
   
   public CompressingContentWriter(ContentWriter realContentWriter, String compressionType)
   {
      this.realContentWriter = realContentWriter;
      this.compressionType = compressionType;
   }

   public String getCompressionType()
   {
      return compressionType;
   }
   
   @Override
   public boolean isChannelOpen()
   {
   }

   @Override
   public boolean isClosed()
   {
      // TODO Auto-generated method stub
      return false;
   }
   
   @Override
   public OutputStream getContentOutputStream() throws ContentIOException
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public FileChannel getFileChannel(boolean arg0) throws ContentIOException
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ContentReader getReader() throws ContentIOException
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public WritableByteChannel getWritableChannel() throws ContentIOException
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void putContent(ContentReader arg0) throws ContentIOException
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void putContent(File arg0) throws ContentIOException
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void putContent(InputStream arg0) throws ContentIOException
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void putContent(String arg0) throws ContentIOException
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void addListener(ContentStreamListener listener)
   {
      realContentWriter.addListener(listener);
   }
   @Override
   public String getContentUrl()
   {
      return realContentWriter.getContentUrl();
   }
   @Override
   public String getEncoding()
   {
      return realContentWriter.getEncoding();
   }
   @Override
   public Locale getLocale()
   {
      return realContentWriter.getLocale();
   }
   @Override
   public String getMimetype()
   {
      return realContentWriter.getMimetype();
   }
   @Override
   public long getSize()
   {
      return realContentWriter.getSize();
   }
   
   @Override
   public void setEncoding(String encoding)
   {
      realContentWriter.setEncoding(encoding);
   }
   @Override
   public void setLocale(Locale locale)
   {
      realContentWriter.setLocale(locale);
   }
   @Override
   public void setMimetype(String mimetype)
   {
      realContentWriter.setMimetype(mimetype);
   }

   @Override
   public void guessEncoding()
   {
      // Not currently supported
      return;
   }
   @Override
   public void guessMimetype(String filename)
   {
      // Not currently supported
      return;
   }
}
