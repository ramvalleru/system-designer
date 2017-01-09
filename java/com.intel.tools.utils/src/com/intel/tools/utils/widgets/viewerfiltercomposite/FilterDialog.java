/* ============================================================================
 * INTEL CONFIDENTIAL
 *
 * Copyright 2016 Intel Corporation All Rights Reserved.
 *
 * The source code contained or described herein and all documents related to
 * the source code ("Material") are owned by Intel Corporation or its suppliers
 * or licensors. Title to the Material remains with Intel Corporation or its
 * suppliers and licensors. The Material contains trade secrets and proprietary
 * and confidential information of Intel or its suppliers and licensors. The
 * Material is protected by worldwide copyright and trade secret laws and
 * treaty provisions. No part of the Material may be used, copied, reproduced,
 * modified, published, uploaded, posted, transmitted, distributed, or
 * disclosed in any way without Intel's prior express written permission.
 *
 * No license under any patent, copyright, trade secret or other intellectual
 * property right is granted to or conferred upon you by disclosure or delivery
 * of the Materials, either expressly, by implication, inducement, estoppel or
 * otherwise. Any license under such intellectual property rights must be
 * express and approved by Intel in writing.
 * ============================================================================
 */
package com.intel.tools.utils.widgets.viewerfiltercomposite;

import java.util.regex.PatternSyntaxException;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.intel.tools.utils.widgets.ErrorHeader;
import com.intel.tools.utils.widgets.ErrorHeader.ErrorLevel;
import com.intel.tools.utils.widgets.SubHeader;

/**
 * Dialog to configure a viewer filter
 */
public class FilterDialog extends Dialog {
    /**
     * The filter to configure
     */
    private final ExtendedViewerFilter filter;

    /**
     * The widgets to select the filter parameters
     */
    private Button caseSensitiveButton;
    private Button regExpButton;
    private Button wholeWordButton;
    private Label wildCard;
    private Text filterText;

    private ErrorHeader errorHeader;

    /**
     * Default constructor
     *
     * @param parentShell
     *            the parent shell
     * @param filter
     *            the filter to configure
     */
    public FilterDialog(final Shell parentShell, final ExtendedViewerFilter filter) {
        super(parentShell);
        this.filter = filter;
    }

    @Override
    protected void okPressed() {
        filter.setFilterString(filterText.getText());
        filter.enableCaseSensitive(caseSensitiveButton.getSelection());
        filter.enableRegEx(regExpButton.getSelection());
        filter.enableWholeWord(wholeWordButton.getSelection());

        try {
            filter.compilePattern();
            setReturnCode(OK);
            close();
        } catch (final PatternSyntaxException pse) {
            // Stay in this dialog until the user corrects its mistake(s)
            errorHeader.setErrorText(pse.getDescription());
            errorHeader.setVisible(true);
        }
    }

    @Override
    protected void configureShell(final Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Configure the filter");
    }

    @Override
    protected Control createDialogArea(final Composite parent) {
        final Composite container = (Composite) super.createDialogArea(parent);
        createUi(container);
        return container;
    }

    /**
     * Create the dialog widgets
     *
     * @param container
     */
    private void createUi(final Composite container) {
        final GridLayout containerLayout = new GridLayout(2, false);
        container.setLayout(containerLayout);

        final Label label = new Label(container, SWT.LEFT);
        label.setText("Filter text :");
        label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));

        filterText = new Text(container, SWT.BORDER);
        filterText.setText(filter.getFilterString());
        filterText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

        wildCard = new Label(container, SWT.LEFT);
        wildCard.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
        wildCard.setSize(0, 0);
        wildCard.setText("(* = any string, ? = any character, \\ = escape for literals: * ? \\)");
        wildCard.setVisible(!filter.isRegEx());

        final SubHeader optionsHeader = new SubHeader(container);
        optionsHeader.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 2, 1));
        optionsHeader.setText("Options");

        caseSensitiveButton = new Button(container, SWT.CHECK);
        caseSensitiveButton.setText("Case sensitive");
        caseSensitiveButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
        caseSensitiveButton.setSelection(filter.isCaseSensitive());

        regExpButton = new Button(container, SWT.CHECK);
        regExpButton.setText("Regular expression");
        regExpButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
        regExpButton.setSelection(filter.isRegEx());
        regExpButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                wholeWordButton.setEnabled(!regExpButton.getSelection());
                wildCard.setVisible(!regExpButton.getSelection());
            }
        });

        wholeWordButton = new Button(container, SWT.CHECK);
        wholeWordButton.setText("Whole word");
        wholeWordButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1));
        wholeWordButton.setSelection(filter.isWholeWord());
        wholeWordButton.setEnabled(!filter.isRegEx());

        // final Composite compo = new Composite(container, SWT.NONE);
        // compo.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 2, 1));
        errorHeader = new ErrorHeader(container);
        errorHeader.setErrorLevel(ErrorLevel.ERROR);
        errorHeader.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
        errorHeader.setErrorText("");
        errorHeader.setVisible(false);

        filterText.setSelection(filterText.getText().length());
    }
}
