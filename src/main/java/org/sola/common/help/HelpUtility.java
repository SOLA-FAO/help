/**
 * ******************************************************************************************
 * Copyright (C) 2012 - Food and Agriculture Organization of the United Nations (FAO).
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice,this list
 *       of conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice,this list
 *       of conditions and the following disclaimer in the documentation and/or other
 *       materials provided with the distribution.
 *    3. Neither the name of FAO nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.common.help;

import java.awt.event.ActionListener;
import java.net.URL;
import java.util.logging.Level;
import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.Popup;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import org.sola.common.logging.LogUtility;
import org.sola.common.messaging.ClientMessage;
import org.sola.common.messaging.MessageUtility;

/**
 *
 * @author dounnaah
 */
public class HelpUtility {

    private HelpSet helpSet = null;
    private HelpBroker helpBroker;
    private String helpTopicName;
    private HelpUtility() {
    }

    private static class HelpUtilityHolder {

        private static final HelpUtility INSTANCE = new HelpUtility();
    }

    public static HelpUtility getInstance() {
        return HelpUtilityHolder.INSTANCE;
    }

   /**
     * Returns a <code>HelpSet<code/> for the construction of an ActionListener.
     * @return Helpset 
     */
    private HelpSet getHelpSet() {
        if (helpSet == null) {
            String pathToHS = "defaultlang/HelpSet.xml";
            String subpath = helpTopicName;
            String initsubpath = subpath.substring(0, 1);
            
            if (initsubpath.contains("1")) {
             String helpsetsubpath = subpath.substring(1, 5);
             pathToHS = "defaultlang/HelpSet"+helpsetsubpath+".xml";   
            }
            
            try {
                URL hsURL = getClass().getResource(pathToHS);
                LogUtility.log("Found HelpSet at " + pathToHS, Level.FINE);
                helpSet = new HelpSet(null, hsURL);
            } catch (Exception ee) {
                System.out.println("HelpSet: " + ee.getMessage());
                System.out.println("HelpSet: " + pathToHS + " not found");
                String[] params = {"" + ee.getMessage(), "" + pathToHS};
                MessageUtility.displayMessage(ClientMessage.EXCEPTION_HELPSET, params);
            }
        }
        return helpSet;
    }



    private HelpBroker getHelpBroker(){
        if(helpBroker == null){
            helpBroker = getHelpSet().createHelpBroker();
        }
        return helpBroker;
    }
    
    /**
     * Returns an <code>ActionListener<code/> for displaying help topics in a help viewer.
     *
     * @param object JButton to which the actionListener is registered to listen for events
     * @param contextMapID MapID of the topic to be displayed
     * @return ActionListener for <code>object</code>
     */
    public ActionListener getHelpListener(JButton object, String contextMapID) {
        CSH.setHelpIDString(object, contextMapID);
        return new CSH.DisplayHelpFromSource(getHelpBroker());
    }
    
    /** 
     * Enables help on the button. If button is clicked, help topic will be displayed. 
     * @param object JButton to which the actionListener is registered to listen for events
     * @param contextMapID MapID of the topic to be displayed
     */
    public void registerHelpButton(JButton object, String contextMapID) {
        getHelpBroker().enableHelpOnButton(object, contextMapID, getHelpSet());
    }
    
    /** 
     * Enables help on the button. If button is clicked, help topic will be displayed. 
     * @param object JMenuItem to which the actionListener is registered to listen for events
     * @param contextMapID MapID of the topic to be displayed
     */
    public void registerHelpMenu(JMenuItem object, String contextMapID) {
        this.helpTopicName = contextMapID;
        getHelpBroker().enableHelpOnButton(object, contextMapID, getHelpSet());
    }

    /** Registers provided panel to display help topic upon F1 key press. */
    public void registerHelpKey(JPanel object, String contextMapID) {
        this.helpTopicName = contextMapID;
        getHelpBroker().enableHelpKey(object, contextMapID, getHelpSet());
    }
    
    /**
     * Returns an <code>ActionListener<code/> for displaying help topics in a help viewer.
     *
     * @param object JMenuItem to which the actionListener is registered to listen for events
     * @param contextMapID MapID of the topic to be displayed
     * @return ActionListener for <code>object</code>
     */
    public ActionListener getHelpListener(JMenuItem object, String contextMapID) {
        CSH.setHelpIDString(object, contextMapID);
        return new CSH.DisplayHelpFromSource(getHelpBroker());
    }
    
    public void showTopic(String contextMapID){
       helpTopicName = contextMapID;
       if(getHelpBroker().isDisplayed()){
            getHelpBroker().setCurrentID(contextMapID); 
        } else {
            getHelpBroker().initPresentation();
            getHelpBroker().setCurrentID(contextMapID);
        }
        ((javax.help.DefaultHelpBroker)getHelpBroker()).setDisplayed(true);
    }

}
