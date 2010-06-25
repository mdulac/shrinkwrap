/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.shrinkwrap.impl.base.exporter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.exporter.ArchiveExportException;
import org.jboss.shrinkwrap.api.exporter.FileExistsException;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;

/**
 * Implementation of ZipExporter used to export an Archive as a Zip format. 
 * 
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class ZipExporterImpl extends AbstractStreamExporterImpl implements ZipExporter
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ZipExporterImpl.class.getName());

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   public ZipExporterImpl(final Archive<?> archive)
   {
      super(archive);
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations - ZipExporter ---------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.exporter.ZipExporter#exportZip()
    */
   @Override
   public InputStream exportZip()
   {
      // Create export delegate
      AbstractExporterDelegate<InputStream> exportDelegate = new JdkZipExporterDelegate(this.getArchive());

      // Export and get result
      return exportDelegate.export();
   }

   /**
    * @see org.jboss.shrinkwrap.api.exporter.ZipExporter#exportZip(java.io.OutputStream)
    */
   @Override
   public void exportZip(final OutputStream target) throws ArchiveExportException, IllegalArgumentException
   {
      // Precondition checks
      if (target == null)
      {
         throw new IllegalArgumentException("Target must be specified");
      }

      // Get Stream
      final InputStream in = this.exportZip();

      // Write out
      try
      {
         IOUtil.copyWithClose(in, target);
      }
      catch (final IOException e)
      {
         throw new ArchiveExportException("Error encountered in exporting archive to " + target, e);
      }
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.exporter.ZipExporter#exportZip(java.io.File, boolean)
    */
   @Override
   public void exportZip(final File target, final boolean overwrite) throws ArchiveExportException,
         FileExistsException, IllegalArgumentException
   {
      // Get stream and perform precondition checks
      final OutputStream out = this.getOutputStreamToFile(target, overwrite);

      // Write out
      this.exportZip(out);
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.exporter.ZipExporter#exportZip(java.io.File)
    */
   @Override
   public void exportZip(final File target) throws ArchiveExportException, FileExistsException,
         IllegalArgumentException
   {
      this.exportZip(target, false);
   }

}
