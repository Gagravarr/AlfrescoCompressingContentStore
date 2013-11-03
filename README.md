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
yet been implemented...

License
=======
The code is available under the Apache License version 2. However, it builds
on top of Alfresco, which is under the LGPL v3 license, so in most cases
the resulting system will fall under the stricter LGPL rules...
