/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.ui.internal.console;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.IPatternMatchListenerDelegate;
import org.eclipse.ui.console.PatternMatchEvent;

public class PatternMatchListener implements IPatternMatchListener {

    private PatternMatchListenerExtension fExtension;
    private IPatternMatchListenerDelegate fDelegate;
    
    public PatternMatchListener(PatternMatchListenerExtension extension) throws CoreException {
        fExtension = extension;
        fDelegate = fExtension.createDelegate();
    }   

    /* (non-Javadoc)
     * @see org.eclipse.ui.console.IPatternMatchListener#getPattern()
     */
    public String getPattern() {
        return fExtension.getPattern();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.console.IPatternMatchListener#getCompilerFlags()
     */
    public int getCompilerFlags() {
        return fExtension.getCompilerFlags();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.console.IPatternMatchListener#matchFound(org.eclipse.ui.console.PatternMatchEvent)
     */
    public void matchFound(PatternMatchEvent event) {
        fDelegate.matchFound(event);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.console.IPatternMatchListener#connect(org.eclipse.ui.console.IConsole)
     */
    public void connect(IConsole console) {
        fDelegate.connect(console);
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.console.IPatternMatchListener#disconnect()
     */
    public void disconnect() {
        fDelegate.disconnect();
    }

}
