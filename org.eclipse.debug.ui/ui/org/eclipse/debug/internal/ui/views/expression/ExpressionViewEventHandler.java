package org.eclipse.debug.internal.ui.views.expression;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */

import java.util.List;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IExpressionListener;
import org.eclipse.debug.core.model.IExpression;
import org.eclipse.debug.internal.ui.views.variables.VariablesViewEventHandler;
import org.eclipse.debug.ui.AbstractDebugView;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
 
/**
 * Updates the expression view
 */ 
public class ExpressionViewEventHandler extends VariablesViewEventHandler implements IExpressionListener {

	/**
	 * Constructs a new event handler on the given view
	 * 
	 * @param view variables view
	 */
	public ExpressionViewEventHandler(AbstractDebugView view) {
		super(view);
		DebugPlugin plugin= DebugPlugin.getDefault();
		plugin.getExpressionManager().addExpressionListener(this);		
	}
	
	/**
	 * De-registers this event handler from the debug model.
	 */
	public void dispose() {
		DebugPlugin plugin= DebugPlugin.getDefault();
		plugin.getExpressionManager().removeExpressionListener(this);
		super.dispose();
	}	
	
	/**
	 * @see IExpressionListener#expressionAdded(IExpression)
	 */
	public void expressionAdded(final IExpression expression) {
		Runnable r = new Runnable() {
			public void run() {
				if (isAvailable()) {
					getTreeViewer().getControl().setFocus();
					insert(expression);
					selectAndReveal(expression);
					getTreeViewer().expandToLevel(expression, 1);
				}
			}
		};
		getView().asyncExec(r);
	}

	/**
	 * @see IExpressionListener#expressionRemoved(IExpression)
	 */
	public void expressionRemoved(final IExpression expression) {
		Runnable r = new Runnable() {
			public void run() {
				if (isAvailable()) {
					remove(expression);
					IContentProvider provider= getTreeViewer().getContentProvider();
					if (provider instanceof ExpressionViewContentProvider) {
						ExpressionViewContentProvider expressionProvider= (ExpressionViewContentProvider) provider;
						List decendants = expressionProvider.getCachedDecendants(expression);
						decendants.add(expression);
						// Remove the parent cache for the expression and its children
						expressionProvider.removeCache(decendants.toArray());
						IExpression[] expressions= DebugPlugin.getDefault().getExpressionManager().getExpressions();
						if (expressions.length > 0) {
							getTreeViewer().setSelection(new StructuredSelection(expressions[0]), true);
						}
					}
				}
			}
		};
		getView().asyncExec(r);
	}

	/**
	 * @see IExpressionListener#expressionChanged(IExpression)
	 */
	public void expressionChanged(final IExpression expression) {
		Runnable r = new Runnable() {
			public void run() {
				refresh(expression);
			}
		};
		getView().asyncExec(r);			
	}
	
	/**
	 * Override the superclass method. Do nothing.
	 */
	protected void doHandleResumeEvent(DebugEvent event) {
	}
}
