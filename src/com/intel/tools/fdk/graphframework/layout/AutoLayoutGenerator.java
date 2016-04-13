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
package com.intel.tools.fdk.graphframework.layout;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionPoint;
import org.eclipse.draw2d.geometry.Rectangle;

import com.intel.tools.fdk.graphframework.displayer.GraphDisplayer;
import com.intel.tools.fdk.graphframework.figure.presenter.LeafPresenter;
import com.intel.tools.fdk.graphframework.graph.adapter.IAdapter;

/**
 * Graph layout which choose a position for each graph node.
 *
 * The algorithm used is the one defined in {@link AutoLayoutComputer}.
 *
 * @param <T>
 *            type of node presenter used
 */
public class AutoLayoutGenerator extends LayoutGenerator {

    /**
     * Create a layout generator which initialize the displayed graph with position computed through a dedicated
     * algorithm. </br>
     *
     * After a graph update notification, the layout algorithm will not be computed again. Only the original graph is
     * computed.
     *
     * @param adapter
     *            the model adapter which provide the graph
     * @param displayer
     *            the graph displayer to use
     */
    public AutoLayoutGenerator(final IAdapter adapter, final GraphDisplayer displayer) {
        super(adapter, displayer);

        // The first display has been done, let's compute initial positions.
        final AutoGroupLayoutComputer computer = new AutoGroupLayoutComputer(adapter.getGraph());
        int widthMax = 0;
        int heightMax = 0;
        for (final LeafPresenter presenters : getLeafPresenters()) {
            final Rectangle bounds = presenters.getBoundsFigure().getBounds();
            widthMax = bounds.width > widthMax ? bounds.width : widthMax;
            heightMax = bounds.height > heightMax ? bounds.height : heightMax;
        }
        for (final LeafPresenter presenter : getLeafPresenters()) {
            final Point coord = computer.getCoordinate(presenter.getNode());
            presenter.getBoundsFigure()
                    .setLocation(new PrecisionPoint(coord.x * widthMax * 3, coord.y * heightMax * 1.5));
        }
    }

}
