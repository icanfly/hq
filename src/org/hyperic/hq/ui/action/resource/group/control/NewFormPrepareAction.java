/*
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2004, 2005, 2006], Hyperic, Inc.
 * This file is part of HQ.
 * 
 * HQ is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 */

package org.hyperic.hq.ui.action.resource.group.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.LabelValueBean;
import org.hyperic.hq.appdef.shared.AppdefEntityID;
import org.hyperic.hq.appdef.shared.AppdefGroupValue;
import org.hyperic.hq.appdef.shared.AppdefResourceValue;
import org.hyperic.hq.bizapp.shared.AppdefBoss;
import org.hyperic.hq.bizapp.shared.ControlBoss;
import org.hyperic.hq.ui.action.BaseAction;
import org.hyperic.hq.ui.beans.OptionItem;
import org.hyperic.hq.ui.util.BizappUtils;
import org.hyperic.hq.ui.util.ContextUtils;
import org.hyperic.hq.ui.util.RequestUtils;

/**
 * An <code>Action</code> subclass that prepares a control action associated
 * with a group.
 */
public class NewFormPrepareAction extends BaseAction {

    // ---------------------------------------------------- Public Methods

    /**
     * Create the control action and associate it with the group.
     * populates resourceOrdering in the GroupControlForm.
     */
    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
        throws Exception {
        
        Log log = LogFactory.getLog(NewFormPrepareAction.class.getName());            
        log.trace("preparing new group control action" );                    

        int sessionId = RequestUtils.getSessionId(request).intValue();
        GroupControlForm gForm = (GroupControlForm)form;        
        ServletContext ctx = getServlet().getServletContext();
        ControlBoss cBoss = ContextUtils.getControlBoss(getServlet().getServletContext());

        AppdefEntityID appdefId = RequestUtils.getEntityId(request);

        List actions = cBoss.getActions(sessionId, appdefId);
        actions = OptionItem.createOptionsList(actions);
        gForm.setControlActions(actions);
        gForm.setNumControlActions(new Integer(actions.size()));

        // get the resource ids associated with this group,
        // create an options list, and associate it with the form
        AppdefBoss aBoss = ContextUtils.getAppdefBoss(ctx);
        AppdefGroupValue group = aBoss.findGroup(sessionId, appdefId);
        List groupMembers 
            = BizappUtils.buildGroupResources(aBoss, sessionId, group);
        Iterator i = groupMembers.iterator();
        ArrayList groupOptions = new ArrayList();
        while (i.hasNext()) {
            AppdefResourceValue arv = (AppdefResourceValue)i.next();
            LabelValueBean lvb 
                = new LabelValueBean(arv.getName(), arv.getId().toString());
            groupOptions.add(lvb);
        }           
        gForm.setResourceOrderingOptions(groupOptions);

        return null;
    } 
}
