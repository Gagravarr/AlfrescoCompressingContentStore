package org.alfresco.repo.content;

import java.nio.channels.ReadableByteChannel;

import org.alfresco.service.cmr.repository.ContentReader;

/**
 * This slightly hacky class lets us work around some
 * rather unhelpful protected permissions on various things
 * in the Content package
 */
public class ContentHelper
{
   public static ContentReader callCreateReader(AbstractContentReader reader)
   {
      return reader.createReader();
   }
   public static ReadableByteChannel callGetDirectReadableChannel(AbstractContentReader reader)
   {
      return reader.getDirectReadableChannel();
   }
}
