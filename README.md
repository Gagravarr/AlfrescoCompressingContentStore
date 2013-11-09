Compressing Content Store for Alfresco
======================================

This, when completed, will be an implementation of an Alfresco ContentStore,
which transparently compresses certain mime types. All other content is
passed through unchanged.

The idea of this is that if you have certain kinds of content in your
repository which are large and mostly text based, you can configure Alfresco
to transparently compress them when writing them into the ContentStore, and
have them transparently decompressed on reading. This allows for reduced
disk storage needs for your select text based content, while leaving all
other content in your repo unaffected.

TODO - Implement
================
This is an idea for the Alfresco Summit Barcelona 2013 Hackday, and has not
yet been fully implemented...

Installation
============
The Compressing Content Store works by overriding and wrapping the normal
Content Store. An example context file is provided for wrapping the regular
FileContentStore from 4.x, but you'll likely need to make some tweaks for
your setup.

Your steps should be
* Identify the real ContentStore to be wrapped
* Tweak the config to rename that bean, then create a compressing one for it
* Tweak the config to set the MimeTypes to compress for, and the algorithm
* Build the AMP including this config
* Apply the AMP to your repository, using the Module Management Tool (MMT)
* Restart, and begin writing new/changed content to be compressed!

Uninstallation
==============
If you decide to uninstall this module, you will then be left with some content
that is compressed in your Content Store, which will then confuse everything
when the normal store starts reading and returning it!

As such, after you uninstall, you will need to go through your content store,
looking for content of the previously configured mimetypes, and checking the
first few bytes. If you hit the magic number / signature of your chosen
compression algorthym, you'll need to decompress and replace.

Performance Implications
========================
Because of the way that Content Readers and Writers work in Alfresco, at the
time you fetch a reader or writer you don't know the mimetype that it'll
apply to! That information only gets provided later...

As such, for all accesses, we have to use a RoutingContent{Reader|Writer} 
which will decide which real one to use as late as possible, once the mimetype
has been clarified. This means that for all cases, there are a couple of 
extra calls, but this should have a pretty minimal impact. (It isn't done
with spring interceptors of anything like that)

For the case where compression is needed, there are two things to note. 
Firstly, the compression / decompression will take some work, the exact amount
depending on the algorthim you use. Secondly, there is no random access
possible within the compressed resource, you can only access sequentially.
For some use cases, that is a big deal, though for most it isn't.

License
=======
The code is available under the Apache License version 2. However, it builds
on top of Alfresco, which is under the LGPL v3 license, so in most cases
the resulting system will fall under the stricter LGPL rules...
