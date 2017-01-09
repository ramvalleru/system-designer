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
package com.intel.tools.fdk.graphframework.figure.node;

import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.graphics.Color;

import com.intel.tools.fdk.graphframework.figure.IGraphFigure;
import com.intel.tools.fdk.graphframework.graph.ILeaf;
import com.intel.tools.fdk.graphframework.graph.Style.IStyleListener;
import com.intel.tools.utils.IntelPalette;

/**
 * Represent a body of a graph node </br>
 *
 * The body has fixed width. The height has a minimum value and grows depending of the node input count.
 */
public class LeafBodyFigure extends RectangleFigure implements IGraphFigure, IStyleListener {

    private static final int LINE_WIDTH = 2;

    private final RectangleFigure selection = new RectangleFigure();

    /** The graph leaf this figure represents */
    private final ILeaf leaf;

    /**
     * Creates a new {@link LeafBodyFigure}
     *
     * @param leaf
     *            the leaf this figure represents
     * @param width
     *            node figure width
     * @param height
     *            node figure height
     */
    public LeafBodyFigure(final ILeaf leaf, final int width, final int height) {
        this.leaf = leaf;

        setSize(new Dimension(width, height));
        setFill(true);
        setAntialias(1);
        setLineWidth(LINE_WIDTH);
        setOutline(false);
        setBackgroundColor(leaf.getStyle().getBackground());
        setForegroundColor(leaf.getStyle().getForeground());

        selection.setAlpha(128);
        selection.setFill(true);
        selection.setOutline(true);
        selection.setLineWidth(0);
        selection.setForegroundColor(IntelPalette.INTEL_BLUE);
        selection.setBackgroundColor(IntelPalette.LIGHT_BLUE);
        selection.setVisible(false);
        selection.setBounds(getBounds());

        add(selection);
        this.leaf.getStyle().addListener(this);
    }

    @Override
    public void select() {
        selection.setVisible(true);
    }

    @Override
    public void unselect() {
        selection.setVisible(false);
    }

    /**
     * @return the graph element associated to this figure
     */
    public ILeaf getLeaf() {
        return leaf;
    }

    @Override
    public void foregroundUpdated(final Color color) {
        setForegroundColor(leaf.getStyle().getForeground());
        invalidate();
    }

    @Override
    public void backgroundUpdated(final Color color) {
        setBackgroundColor(leaf.getStyle().getBackground());
        invalidate();
    }
}