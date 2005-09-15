/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.internal.ui.treeviewer;

/**
 * Provides labels for elements. Note that implementations
 * are intended to provide labels asynchronously. 
 * <p>
 * Clients may implement this interface.
 * </p>
 * @since 3.2
 */
public interface IAsynchronousLabelAdapter {
    
    /**
     * Asynchronously retrieves the label of the given object reporting to
     * the given monitor. If unable to retrieve label information, an exception should be
     * reported to the monitor with an appropriate status.
     *  
     * @param object the element for which a label is requested
     * @param context the context in which the label has been requested
     * @param monitor presentation request monitor to report the result to
     */
    public void retrieveLabel(Object object, IPresentationContext context, ILabelRequestMonitor result);
    

}
