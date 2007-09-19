/*******************************************************************************
 * Copyright (c) 2006, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.debug.internal.ui.viewers.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.internal.ui.viewers.model.provisional.IElementContentProvider;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IHasChildrenUpdate;
import org.eclipse.debug.internal.ui.viewers.model.provisional.IPresentationContext;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;

/**
 * @since 3.3
 */
class HasChildrenUpdate extends ViewerUpdateMonitor implements IHasChildrenUpdate {

	private boolean fHasChildren = false;
	
	private List fBatchedRequests = null;
	
	/**
	 * @param contentProvider
	 */
	public HasChildrenUpdate(ModelContentProvider contentProvider, TreePath elementPath, Object element, IElementContentProvider elementContentProvider, IPresentationContext context) {
		super(contentProvider, elementPath, element, elementContentProvider, context);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.model.provisional.viewers.ViewerUpdateMonitor#performUpdate()
	 */
	protected void performUpdate() {
		ModelContentProvider contentProvider = getContentProvider();
		TreePath elementPath = getElementPath();
		if (!fHasChildren) {
			contentProvider.clearFilters(elementPath);
		}
		if (ModelContentProvider.DEBUG_CONTENT_PROVIDER) {
			System.out.println("setHasChildren(" + getElement() + " >> " + fHasChildren); //$NON-NLS-1$ //$NON-NLS-2$
		}
		((TreeViewer)(contentProvider.getViewer())).setHasChildren(elementPath, fHasChildren);
		if (fHasChildren) {
			((InternalTreeModelViewer)contentProvider.getViewer()).autoExpand(elementPath);
		}
		if (elementPath.getSegmentCount() > 0) {
			contentProvider.doRestore(elementPath);
		}
	}

	public void setHasChilren(boolean hasChildren) {
		fHasChildren = hasChildren;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("IHasChildrenUpdate: "); //$NON-NLS-1$
		buf.append(getElement());
		return buf.toString();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.model.ViewerUpdateMonitor#coalesce(org.eclipse.debug.internal.ui.viewers.model.ViewerUpdateMonitor)
	 */
	boolean coalesce(ViewerUpdateMonitor request) {
		if (request instanceof HasChildrenUpdate) {
			if (getElementPath().equals(request.getElementPath())) {
				// duplicate request
				return true;
			} else if (getElementContentProvider().equals(request.getElementContentProvider())) {
				if (fBatchedRequests == null) {
					fBatchedRequests = new ArrayList();
					fBatchedRequests.add(this);
				}
				fBatchedRequests.add(request);
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.model.ViewerUpdateMonitor#startRequest()
	 */
	void startRequest() {
		if (fBatchedRequests == null) {
			getElementContentProvider().update(new IHasChildrenUpdate[]{this});
		} else {
			IHasChildrenUpdate[] updates = (IHasChildrenUpdate[]) fBatchedRequests.toArray(new IHasChildrenUpdate[fBatchedRequests.size()]);
			// notify that the other updates have also started to ensure correct sequence
			// of model updates - **** start at index 1 since the first (0) update has
			// already notified the content provider that it has started.
			for (int i = 1; i < updates.length; i++) {
				getContentProvider().updateStarted((ViewerUpdateMonitor) updates[i]);
			}
			getElementContentProvider().update(updates);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.model.ViewerUpdateMonitor#getPriority()
	 */
	int getPriority() {
		return 1;
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.debug.internal.ui.viewers.model.ViewerUpdateMonitor#getSchedulingPath()
	 */
	TreePath getSchedulingPath() {
		TreePath path = getElementPath();
		if (path.getSegmentCount() > 0) {
			return path.getParentPath();
		}
		return path;
	}		
}