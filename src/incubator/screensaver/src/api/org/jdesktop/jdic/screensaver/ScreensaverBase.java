// Copyright (c) 2004-2005 Sun Microsystems, Inc. All rights reserved. Use is
// subject to license terms.
// 
// This program is free software; you can redistribute it and/or modify
// it under the terms of the Lesser GNU General Public License as
// published by the Free Software Foundation; either version 2 of the
// License, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
// USA

package org.jdesktop.jdic.screensaver;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class for screensavers written in Java.
 * All developers should extend either <code>SimpleScreensaver</code>
 * or <code>JOGLScreensaver</code>.  This base class makes it possible to 
 * offer different types of screensavers in the future, such as those that 
 * can control their own rendering frequency.
 *
 * @see SimpleScreensaver
 * @see JOGLScreensaver
 * @author Mark Roth
 */
public abstract class ScreensaverBase {
    
    /** Logging support */
    private static Logger logger = 
        Logger.getLogger("org.jdesktop.jdic.screensaver");
    
    /** Context information for this screensaver (eg window size) */
    protected ScreensaverContext context;
    
    /** Last dimensions - if they change we need to clear the screen */
    private Dimension lastSize;
    
    /**
     * Called from native code to do initialization.  Subclasses should 
     * override the init() method to provide any initialization.
     *
     * @param context Context information for this screensaver (eg window size)
     */
    public final void baseInit( ScreensaverContext context ) {
        logger.log(Level.FINEST, "Screensaver starting up.");
        this.context = context;
        reinit();
    }
    
    /**
     * Called from native code to destroy the screensaver.  Subclasses 
     * should override the destroy() method to provide any cleanup behavior.
     */
    public final void baseDestroy() {
        logger.log(Level.FINEST, "Screensaver shutting down.");
        try {
            destroy();
        }
        catch(Throwable t) {
            logger.log(Level.WARNING, 
                "Exception occurred during screensaver destroy()", t);
        }
    }
    
    /**
     * Gets the ScreenSaver context object
     *
     * @return The context object for this screen saver.
     */
    public ScreensaverContext getContext() {
        return context;
    }
    
    /**
     * Called from the native layer to render the next frame
     */
    public void renderFrame() {
        Component c = context.getComponent();
        if( (lastSize == null) || 
            (c.getWidth() != lastSize.width) ||
            (c.getHeight() != lastSize.height) )
        {
            lastSize = new Dimension( c.getWidth(), c.getHeight() );
            reinit();
        }
    }
    
    /**
     * Initialize or reinitialize the screensaver.  Calls subclass init().
     * Any exceptions thrown during init() will be sent to the 
     * org.jdesktop.jdic.screensaver J2SE logger.
     */
    private void reinit() {
        try {
            init();
        }
        catch(Throwable t) {
            logger.log(Level.WARNING, 
                "Exception occurred during screensaver init()", t);
        }
    }
    
    /**
     * Subclasses can optionally override this method to perform any 
     * initialization.  Default does nothing.
     * <p>
     * Init will be called once at the beginning, before the first paint,
     * and zero or more times during the running to reset the screensaver
     * (for example if the resolution changed).
     */
    protected void init() {
    }
    
    /**
     * Subclasses can optionally override this method to perform any
     * cleanup before the screensaver is destroyed.  Default does nothing.
     * <p>
     * Destroy will be called at most once.  After a call to this
     * method, the screensaver should not expect the ScreensaverContext
     * to be valid any longer.
     */
    protected void destroy() {
    }
    
}
