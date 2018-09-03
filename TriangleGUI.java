/*============================================================================*
 TriangleGUI.java
 
 A GUI to enter and display triangle data.
 It includse a graphic representation of a default inital triangle and the 
 resulting triangle scaled to fit the space of the initial default.
 *============================================================================*/

import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics;
import javax.swing.*;
import javax.swing.text.*;

/**===========================================================================*
 * <p>
 TriangleGUI class
 * <p>
 * This probably turned into a kitchen sink.
 * It 
 *============================================================================*/
public class TriangleGUI 
    extends    JFrame 
    implements ActionListener, ComponentListener
{
    ////////////////////////////////////////////////////////////////////////////
    // Member variables...
    ////////////////////////////////////////////////////////////////////////////
    // &&& tacky way to get to this object from contained classes...
    TriangleGUI mTriangleGUI = this;
    
    // &&& Figure out what these fixups really represent
    protected final int GOK_PIXELS   = 7;
    protected final int TB_HEIGHT    = 30; // Top Bar Height
    
    protected final int    NUM_DECIMAL_PLACES  = 4;
    protected final int    NUM_ROUNDING_PLACES = 12;
    protected final double ROUND_FACTOR        = Math.pow(10, NUM_ROUNDING_PLACES);
    
    /*========================================================================*
     Formatter stuff...
     *========================================================================*/
    private NumberFormat valueDisplayFormat;
    private NumberFormat valueEditFormat;
    
    /*========================================================================*
     Base Panel data...
     *========================================================================*/
    protected int    mFrameWidth  = 500;
    protected int    mFrameHeight = 500;
    
    protected JPanel mBasePanel = null; // holds everything
      
    /*========================================================================*
     Button Panel data...
     *========================================================================*/
    // private ButtonPanel mButtonPanel  = null; // holds buttons
    private JPanel  mButtonPanel  = null; // holds buttons
    
    // &&& the buttons and labels could probably be members of ButtonPanel
    private JButton mCalcButton   = null;
    private JLabel  mCalcLabel    = null;
    
    private JButton mResetButton  = null;
    private JLabel  mResetLabel   = null;
    
    protected int   mButtonPanelHeight = 0;

    /*========================================================================*
     Data Panel data...
     *========================================================================*/
    protected Triangle    mTriangle   = null;
    
    protected DataPanel   mDataPanel  = null; // holds data input fields

    // &&& Probably needs to be member data of DataPanel...
    protected DataField[] mSideField  = new DataField[3];
    protected DataField[] mAngleField = new DataField[3];
    
    /*========================================================================*
     Graphic Panel data...
     *========================================================================*/
    private GraphicsPanel  mGraphicPanel  = null; // holds triangle graphic
    
    /**=======================================================================*
     <p>
     * TriangleGUI() constructor
     * 
     * @param aTriangle Triangle object that the UI is to operate on.
     <p>
     *========================================================================*/
    TriangleGUI
    ( Triangle aTriangle
    )
    {
        super("Triangle Solutions GUI");
        
        // Get screen size using the Toolkit class
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    
        int screenWidth  = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
    
        // Center the frame on the screen...
        int frameX      = ( (screenWidth  / 2) 
                        - (mFrameWidth  / 2)
                        );
        int frameY      = ( (screenHeight / 2) 
                        - (mFrameHeight / 2)
                        );

        mTriangle = aTriangle;
        
        setUpFormats();

        // &&& this may change when we get resize() working...
        setBounds( frameX
                 , frameY
                 , mFrameWidth  + (GOK_PIXELS * 2)
                 , mFrameHeight + (GOK_PIXELS + TB_HEIGHT)
                 );
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container con = this.getContentPane(); // inherit main frame
        
        con.addComponentListener(this);
        
        mBasePanel = createContentPane();

        con.add(mBasePanel); // add the base panel to frame
        
        mButtonPanel.requestFocus();
        
        // buttonPanel.setVisible(true);
        setVisible(true); // display this frame
    } // TriangleGUI constructor

    /*========================================================================*
    Since ComponentListener is an abstract class, we need to implement some
    methods to keep it happy...
     *========================================================================*/
    public void componentHidden(ComponentEvent ce) {};
    public void componentShown (ComponentEvent ce) {};
    public void componentMoved (ComponentEvent ce) {};
 
    /**=======================================================================*
     <p>
     componentResized()
     <p>
     * When the UI window is resized we reset the frame height and width that
     * all the other elements have been located in reference to.
     * 
     * @param ce The ComponentEvent
     <p>
     *========================================================================*/
    public void componentResized(ComponentEvent ce)
    {
        mFrameHeight = this.getHeight();
        mFrameWidth  = this.getWidth();
        mDataPanel.calcLocations();
        // mButtonPanel.calcLocations();

    } // componentResized()

    /**=======================================================================*
     <p>
     createContentPane()
     <p>
     * Creates the pane that contains all the UI's content
     * 
     * @param (none)
     * @return A JPanel with a base, Button and Data panel
     <p>
     *========================================================================*/
    private JPanel createContentPane()
    {
        JPanel basePanel  = new JPanel();
        basePanel.setLayout(null);
            
        // mButtonPanel = new ButtonPanel();
        mButtonPanel = createButtonPanel();
        basePanel.add(mButtonPanel);

        mDataPanel   = new DataPanel();
        basePanel.add(mDataPanel);

        basePanel.setOpaque(true);
        
        return basePanel;

    } // createContentPane()
    
    /**=======================================================================*
     <p>
     nested class DataField
     <p>
     Contains everything associated with a data input field.
     <p>
     * This is then used to create arrays of fields that can be iterated through
     * as opposed to individual explicitly referenced field items.
     <p>
     * &&&
     * Does this need to add a PropertyChangeListener?
     <p>
     *========================================================================*/
    protected class DataField
    {
        final String STR_SIDE  = "Side " ;
        final String STR_ANGLE = "Angle ";
        //  dataField
        public JFormattedTextField mTextField = null;
        //  label
        public JLabel              mLabel     = null;
        //  angle/side        
        public boolean             mIsAngle   = false;
        private int                mHeight    = 0;
        
         /**===================================================================*
         <p>
         DataField() constructor
         <p>
        * @param xPos    X position (relative to the host frame)
        * @param yPos    Y position (relative to the host frame)
        * @param width   field width
        * @param height  field height
        * @param index   Array index of this object in mSide/AngleField array
        * @param isAngle true if this is an angle false if this is a side
        * 
        * @return New DataField object
         <p>
         *====================================================================*/
        DataField
        ( int     xPos
        , int     yPos
        , int     width
        , int     height
        , int     index
        , boolean isAngle
        )
        {
            mHeight = height;
            
            mTextField = createDataField
                ( xPos
                , yPos
                , width
                , height
                );
            mLabel    = createDataLabel
                ( xPos
                , yPos
                , width
                , height
                , ( (isAngle) 
                  ? STR_ANGLE 
                  : STR_SIDE 
                  ) + Character.toString((char)(index + 65))
                );
            mIsAngle   = isAngle;
            
        } // DataField() constructor

        /**===================================================================*
         <p>
         createDataField()
         <p>
         Sets up a data field...
         <p>
         This takes the same parameters with the came VALUES as the associated
         createDataLabel() call (except for the missing label text).
         <p>
         The label goes on TOP of the field, so the field height and yPos values
         get fixed up here.

         * @param xPos   X position (relative to the host frame)
         * @param yPos   Y position (relative to the host frame)
         * @param width  field width
         * @param height field height
         * 
         * @return New JFormattedTextField object
         <p>
         *====================================================================*/
        private JFormattedTextField createDataField
        ( int    xPos
        , int    yPos
        , int    width
        , int    height
        )
        {
            JFormattedTextField field = new JFormattedTextField
                ( new DefaultFormatterFactory
                    ( new NumberFormatter(valueDisplayFormat)
                    , new NumberFormatter(valueDisplayFormat)
                    , new NumberFormatter(valueEditFormat)
                    )
                );

            field.setValue( new Double( 0.0 ) );
            field.setHorizontalAlignment(JTextField.TRAILING);
            field.setEditable(true);

            field.setSize( width
                         , height
                         );
            field.setLocation( xPos
                             , yPos + height
                             );
            return field;

        } // createDataField()

        /**===================================================================*
         <p>
         createDataLabel()
         <p>
         Sets up a data label...
         <p>
         This takes the same parameters with the came VALUES as the associated
         createDataField() call.
         <p>
         The label goes on TOP of the field, so the height and yPos values get
         fixed up in the createDataField() call.

         * @param xPos   X position (relative to the host frame)
         * @param yPos   Y position (relative to the host frame)
         * @param width  field width
         * @param height field height
         * @param labelText Text to be displayed above the associated field
         * 
         * @return New JLabel object
         <p>
         *====================================================================*/
        private JLabel createDataLabel
        ( int    xPos
        , int    yPos
        , int    width
        , int    height
        , String labelText
        )
        {
            JLabel label = new JLabel(labelText);

            label.setSize( width
                         , height
                         );
            label.setLocation( xPos
                             , yPos
                             );
            return label;

        } // createDataLabel()
        
        /**===================================================================*
         <p>
         setLocation()
         <p>
         Sets the location of the label and text field relative to the host frame.
         <p>
         * @param xPos   X position (relative to the host frame)
         * @param yPos   Y position (relative to the host frame)
         * 
         <p>
         *====================================================================*/
        private void setLocation
        ( int    xPos
        , int    yPos
        )
        {
            mTextField.setLocation(xPos, yPos + mHeight);
            mLabel    .setLocation(xPos, yPos);
        } // setLocation()
        
    } // class DataField
    
    /**=======================================================================*
     <p>
     nested class DataPanel
     <p>
     Contains all the position information necessary to set and resize the data
     * panel.  Includes calcLocations() to calc/recalc the size and position of
     * the contained components.
     <p>
     *========================================================================*/
    protected class DataPanel
        extends JPanel
    {
        int mDataPanelWidth  = 0;
        int mDataPanelHeight = 0;
        
        final int mFieldWidth  = 70;
        final int mFieldHeight = 30;
        
        final int mTopPadding  = 10;
        final int mBtmPadding  = mTopPadding;
        final int mSidePadding = 20;
        final int mTopRowY     = mTopPadding;
        
        int mMidRowY    = 0;
        int mBtmRowY    = 0;
        int mLftX       = 0;
        int mMidLftX    = 0;
        int mMidX       = 0;
        int mMidRgtX    = 0;
        int mRgtX       = 0;
        
        /**===================================================================*
        <p>
        DataPanel() constructor
        <p>
        Sets up the data fields...
        <p>
        Since this takes up all the vertical space that the button panel *doesn't*
        take, we need to create the button panel *first* in order to get the value
        of mButtonPanelHeight

        * @param (none)
        * @return A DataPanel containing the data fields and a graphic overlay
         <p>
         *====================================================================*/
        DataPanel()
        {
            super();

            ////////////////////////////////////////////////////////////////////
            // Set up the panel to put the data fields on...
            ////////////////////////////////////////////////////////////////////
            setLayout(null);
            setLocation( 0
                       , 0
                       );

            ////////////////////////////////////////////////////////////////////
            // Create data fields...
            ////////////////////////////////////////////////////////////////////
            /*----------------------------------------------------------------*
                                 AngleC
                        SideB              SideA
                AngleA           SideC             AngleB
             Create fields in the counter clockwise direction starting at AngleA...
             *----------------------------------------------------------------*/
            mAngleField[0] = new DataField
                         ( mLftX
                         , mBtmRowY
                         , mFieldWidth
                         , mFieldHeight
                         , 0
                         , true // is angle
                         );
            /*
            mDataAngleA.addPropertyChangeListener( STR_ANGLE_A
                                                 , new FormattedTextFieldListener()
                                                 );
            */

            mSideField[2] = new DataField
                         ( mMidX
                         , mBtmRowY
                         , mFieldWidth
                         , mFieldHeight
                         , 2
                         , false // is not angle
                         );

            mAngleField[1] = new DataField
                         ( mRgtX
                         , mBtmRowY
                         , mFieldWidth
                         , mFieldHeight
                         , 1
                         , true
                         );

            mSideField[0] = new DataField
                         ( mMidRgtX
                         , mMidRowY
                         , mFieldWidth
                         , mFieldHeight
                         , 0
                         , false
                         );

            mAngleField[2] = new DataField
                         ( mMidX
                         , mTopRowY
                         , mFieldWidth
                         , mFieldHeight
                         , 2
                         , true
                         );

            mSideField[1] = new DataField
                         ( mMidLftX
                         , mMidRowY
                         , mFieldWidth
                         , mFieldHeight
                         , 1
                         , false
                         );

            for ( int i=0; i < mAngleField.length ; i++ )
            {
                add( mAngleField[i].mTextField );
                add( mAngleField[i].mLabel     );
                // Assumption that we have the same number of angles and sides
                add( mSideField [i].mTextField );
                add( mSideField [i].mLabel     );
            } // for each field add it to the panel
            
            mGraphicPanel = new GraphicsPanel();
            mGraphicPanel.setLayout(null);
            mGraphicPanel.setOpaque(false);

            calcLocations();

            add( mGraphicPanel );

        } // DataPanel() constructor

        /**===================================================================*
         <p>
         calcLocations()
         <p>
         * Calculates the locations of all the UI elements on the DataPanel.
         * Refers to the following variables which need to be calculated first...
         <p>
         * mFrameWidth
         <p>
         * mFrameHeight
         <p>
         * mButtonPanelHeight
         <p>
         * Note that this sets the positions of all the DataField objects and
         * the mGraphicsPanel so, it needs to be called AFTER they are constructed.
         *====================================================================*/
        protected void calcLocations()
        {
            mDataPanelWidth  = mFrameWidth;
            mDataPanelHeight = mFrameHeight - mButtonPanelHeight;
        
            mMidRowY    = ( ( mDataPanelHeight - mTopPadding - mBtmPadding ) / 2 )
                          - mFieldHeight + mTopPadding;
            mBtmRowY    = mDataPanelHeight - (2 * mFieldHeight) - mBtmPadding;

            mLftX       = mSidePadding;
            mMidLftX    = ( mDataPanelWidth / 4 )
                        - ( mFieldWidth     / 2 );
            mMidX       = ( mDataPanelWidth / 2 )
                        - ( mFieldWidth     / 2 );
            mMidRgtX    = ( ( mDataPanelWidth / 4 ) * 3 )
                        - ( mFieldWidth     / 2 );
            mRgtX       = ( mDataPanelWidth - mFieldWidth - mSidePadding );
        
            setSize( mDataPanelWidth
                   , mDataPanelHeight
                   );

            ////////////////////////////////////////////////////////////////////
            // Position data fields...
            ////////////////////////////////////////////////////////////////////
            /*----------------------------------------------------------------*
                                 AngleC
                        SideB              SideA
                AngleA           SideC             AngleB
             Create fields in the counter clockwise direction starting at AngleA...
             *----------------------------------------------------------------*/
            mAngleField[0].setLocation
                         ( mLftX
                         , mBtmRowY
                         );

            mSideField[2].setLocation
                         ( mMidX
                         , mBtmRowY
                         );

            mAngleField[1].setLocation
                         ( mRgtX
                         , mBtmRowY
                         );

            mSideField[0].setLocation
                         ( mMidRgtX
                         , mMidRowY
                         );

            mAngleField[2].setLocation
                         ( mMidX
                         , mTopRowY
                         );

            mSideField[1].setLocation
                         ( mMidLftX
                         , mMidRowY
                         );

            mGraphicPanel.setLocation( mFieldWidth + mSidePadding
                                     , mTopPadding + (2 * mFieldHeight)
                                     );
            mGraphicPanel.setSize( mDataPanelWidth
                                 - (( mFieldWidth + mSidePadding ) * 2 )
                                 , mDataPanelHeight
                                 - (4 * mFieldHeight)
                                 - mTopPadding
                                 - mBtmPadding
                                 );
            mGraphicPanel.calcSize();

        } // calcLocations() 
        
    } // class DataPanel

    private JPanel createButtonPanel()
    {
        final int vertPad           = 10; // above and below buttons
        final int horzPad           = 10; // between buttons

        final int buttonWidth       = 100;
        final int buttonHeight      = 30;

        final int buttonCount       = 2;
        final int buttonPanelWidth  = mFrameWidth;
        mButtonPanelHeight          = buttonHeight + vertPad;
        
        JPanel buttonPanel          = new JPanel();
        
        ////////////////////////////////////////////////////////////////////////
        // Create the panel to put the buttons on...
        ////////////////////////////////////////////////////////////////////////
        buttonPanel.setLayout(null);
        buttonPanel.setLocation( ( mFrameWidth  - buttonPanelWidth ) / 2
                               ,   mFrameHeight - ( buttonHeight + vertPad )
                               );
        buttonPanel.setSize( buttonPanelWidth
                           , mButtonPanelHeight
                           );
        
        ////////////////////////////////////////////////////////////////////////
        // Create calc button...
        ////////////////////////////////////////////////////////////////////////
        mCalcButton = new JButton("Calculate");
        mCalcLabel  = new JLabel("");
        
        mCalcButton.setMnemonic(99);
        mCalcButton.addActionListener(this);

        mCalcButton.setSize( buttonWidth
                           , buttonHeight  
                           );
        // &&& needs to be in calcLocations()
        int calcButtonX = ( buttonPanelWidth 
                          - ( ( buttonWidth * buttonCount )     // buttons width
                            + ( ( buttonCount - 1 ) * horzPad ) // padding width
                            )
                          ) / 2;

        mCalcButton.setLocation( calcButtonX
                               , vertPad / 2 
                               );

        buttonPanel.add(mCalcLabel);
        buttonPanel.add(mCalcButton);

        ////////////////////////////////////////////////////////////////////////
        // Create reset button...
        ////////////////////////////////////////////////////////////////////////
        mResetButton = new JButton("Reset");
        mResetLabel  = new JLabel("");
        
        mResetButton.setMnemonic(114);
        mResetButton.addActionListener(this);

        mResetButton.setSize( buttonWidth
                            , buttonHeight  
                            );
        // &&& needs to be in calcLocations()
        mResetButton.setLocation( calcButtonX + buttonWidth + horzPad // reset to right of calc
                                , vertPad / 2 
                                );
        
        buttonPanel.add(mResetLabel);
        buttonPanel.add(mResetButton);

        // buttonPanel.setOpaque(true);
        
        return buttonPanel;
        
    } // createButtonPanel()

    /**=======================================================================*
     <p>
     nested class ButtonPanel
     <p>
     Contains all the position information necessary to set and resize the button
     * panel.  Includes calcLocations() to calc/recalc the size and position of
     * the contained components.
     <p>
     *========================================================================*/
    protected class ButtonPanel
        extends JPanel
    {
        final int mVertPad           = 10; // above and below buttons
        final int mHorzPad           = 10; // between buttons

        final int mButtonWidth       = 100;
        final int mButtonHeight      = 30;

        final int mButtonCount       = 2;
        int       mButtonPanelWidth  = 0;
        
        /**===================================================================*
        <p>
        ButtonPanel() constructor
        <p>
        Sets up the buttons...
        <p>
        Since this takes up all the vertical space that the button panel *doesn't*
        take, we need to create the button panel *first* in order to get the value
        of mButtonPanelHeight

        * @param (none)
        * @return A DataPanel containing the data fields and a graphic overlay
         <p>
         *====================================================================*/
        ButtonPanel()
        {
            super();
            
            mButtonPanelHeight = mButtonHeight + mVertPad;

            ////////////////////////////////////////////////////////////////////
            // Setup the panel to put the buttons on...
            ////////////////////////////////////////////////////////////////////
            setLayout(null);

            ////////////////////////////////////////////////////////////////////
            // Create calc button...
            ////////////////////////////////////////////////////////////////////
            mCalcButton = new JButton("Calculate");
            mCalcLabel  = new JLabel("");

            mCalcButton.setMnemonic(99);
            mCalcButton.addActionListener(mTriangleGUI);

            mCalcButton.setSize( mButtonWidth
                               , mButtonHeight  
                               );

            ////////////////////////////////////////////////////////////////////
            // Create reset button...
            ////////////////////////////////////////////////////////////////////
            mResetButton = new JButton("Reset");
            mResetLabel  = new JLabel("");

            mResetButton.setMnemonic(114);
            mResetButton.addActionListener(mTriangleGUI);

            mResetButton.setSize( mButtonWidth
                                , mButtonHeight  
                                );
            calcLocations();

            add(mCalcLabel);
            add(mCalcButton);
            add(mResetLabel);
            add(mResetButton);
            
            setOpaque(true);
            
        } // ButtonPanel() constructor
        
        /**===================================================================*
         <p>
         calcLocations()
         <p>
         * Calculates the locations of all the UI elements on the ButtonPanel.
         * Refers to the following variables which need to be calculated first...
         <p>
         * mFrameWidth
         <p>
         * mFrameHeight
         <p>
         * Note that this needs to be called BEFORE mDataPanel since
         * mButtonPanelHeight is a dependency.
         *====================================================================*/
        protected void calcLocations()
        {
            mButtonPanelWidth = mFrameWidth;

            setLocation( 0
                       , mFrameHeight - mButtonPanelHeight
                       );
            setSize( mButtonPanelWidth
                   , mButtonPanelHeight
                   );

            int calcButtonX = ( mButtonPanelWidth 
                              - ( ( mButtonWidth * mButtonCount )     // buttons width
                                + ( ( mButtonCount - 1 ) * mHorzPad ) // padding width
                                )
                              ) / 2;

            mCalcButton.setLocation( calcButtonX
                                   , mVertPad / 2 
                                   );

            mResetButton.setLocation( calcButtonX 
                                    + mButtonWidth 
                                    + mHorzPad // reset to right of calc
                                    , mVertPad / 2 
                                    );
        } // calcLocations()
        
    } // class ButtonPanel
    
    /**=======================================================================*
     <p>
     setUpFormats()
     <p>
     * This is supposed to deal with stupid amounts of digits after the decimal
     * place in displayed doubles.
     <p>
     *========================================================================*/
    private void setUpFormats()
    {
        valueDisplayFormat = NumberFormat.getNumberInstance();
        // this works to display two fractional digits even if they're zero
        valueDisplayFormat.setMinimumFractionDigits(2);
        // &&& So, why doesn't this work such that I don't have to use roundDouble()?
        valueDisplayFormat.setMaximumFractionDigits(NUM_DECIMAL_PLACES);
        
        /*--------------------------------------------------------------------*
         &&&
         After you tab out of a field, the format gets adjusted correctly.
         It's messed up (not formatted) after you hit the calc button.  It's not
         just the long fractional stuff.  Whole numbers show up as n.0 instead
         of n.00.
         Is there something that needs to happen in field.setText() in actionPerformed()?
         *--------------------------------------------------------------------*/
        
        valueEditFormat = NumberFormat.getNumberInstance();
        valueEditFormat.setMinimumFractionDigits(2);
        valueEditFormat.setMaximumFractionDigits(NUM_DECIMAL_PLACES);
        
    } // setUpFormats()
    
    /**=======================================================================*
     <p>
     roundDouble()
     <p>
     The intent here is to round the result to sidestep floating point inaccuracy
     beyond the 13th decimal place.
     * <p>
     * In actual operation, it was used as a hack to keep fields from displaying
     * stupid numbers of digits past the decimal place.  setUpFormats() was
     * supposed to mitigate that, but it isn't yet working in all cases.
     * Note that currently, it's just acting as a pass through.
     <p>
     * @param aDouble the value to be rounded
     * @return the rounded value
     * @see ROUND_FACTOR
     *========================================================================*/
    private double roundDouble
    ( double aDouble
    )
    {
        // return Math.round( aDouble * ROUND_FACTOR) / ROUND_FACTOR;
        return aDouble;
        
    } // roundDouble
  
    /**=======================================================================*
     <p>
     actionPerformed()
     <p>
     *========================================================================*/
    public void actionPerformed
    ( ActionEvent event
    )
    {
        Object source = event.getSource();
        
        if ( source == mCalcButton )
        {
            mCalcLabel.setText("Calc Requested");
    
            /*----------------------------------------------------------------*
            Pull values from the UI fields...
            *-----------------------------------------------------------------*/
            for ( TriangleData.DataID dID: TriangleData.DataID.values() )
            {
                if ( dID != TriangleData.DataID.DATA_INVALID )
                {			
                    mTriangle.angles.set( dID 
                                        , Double.parseDouble
                                          ( mAngleField[dID.ordinal()].mTextField.getText() 
                                          )
                                        );
                    mTriangle.sides.set ( dID 
                                        , Double.parseDouble
                                          ( mSideField [dID.ordinal()].mTextField.getText() 
                                          )
                                        );
                } // if not invalid
            } // for each angle & side
    
            /*----------------------------------------------------------------*
             Get the solution...
             *----------------------------------------------------------------*/
            boolean success = mTriangle.findSolution();
            
            /*----------------------------------------------------------------*
             Display the result...
             *----------------------------------------------------------------*/
            if ( success )
            {
                for ( TriangleData.DataID dID: TriangleData.DataID.values() )
                {
                    if ( dID != TriangleData.DataID.DATA_INVALID )
                    {			
                        mAngleField[dID.ordinal()].mTextField.setText
                        ( String.valueOf
                            ( roundDouble
                                ( mTriangle.angles.get( dID )
                                )
                            )
                        );
                        
                        mSideField[dID.ordinal()].mTextField.setText
                        ( String.valueOf
                            ( roundDouble
                                ( mTriangle.sides.get( dID )
                                )
                            )
                        );
                    } // if not invalid
                } // for each angle & side
                
                mGraphicPanel.calcVerticies();
              
            } // if solution succeeded
            else
            {
                JOptionPane.showMessageDialog( null
                                             , "Failed to find a solution"
                                             , "Error"
                                             , JOptionPane.PLAIN_MESSAGE
                                             );
                setVisible(true);
            } // else solution failed

        } // if mCalcButton
        else if ( source == mResetButton )
        {
            mResetLabel.setText("Reset");

            // Reset all data fields to zero
            for ( TriangleData.DataID dID: TriangleData.DataID.values() )
            {
                if ( dID != TriangleData.DataID.DATA_INVALID )
                {			
                    mAngleField[dID.ordinal()].mTextField.setValue
                    ( new Double( 0.0 )
                    );

                    mSideField [dID.ordinal()].mTextField.setValue
                    ( new Double( 0.0 )
                    );
                } // if not invalid
            } // for each angle & side

            mTriangle.setAll( 0.0
                            , 0.0
                            , 0.0
                            , 0.0
                            , 0.0
                            , 0.0
                            );

            mGraphicPanel.calcVerticies();

        } // if mResetButton
        
    } // actionPerformed()
    
    /*
    class FormattedTextFieldListener implements PropertyChangeListener 
    {
        public void propertyChanged(PropertyChangeEvent e) 
        {
            Object source = e.getSource();
            if (source == mDataAngleA ) {
                amount = ((Number)mDataAngleA.getValue()).doubleValue();
                // ...
            }
        //re-compute payment and update field...
        }
    } // class FormattedTextFieldListener
    */

    /**=======================================================================*
     <p>
     nested class GraphicsPanel
     <p>
     Panel that hosts the triangle graphic embedded in the dataPanel (mDataPanel).
     <p>
     We needed a subclass where we could override paint() and get stuff painted.   
     <p>
     *========================================================================*/
    protected class GraphicsPanel
        extends JPanel
    {
        /*--------------------------------------------------------------------*
        The width and height of the bounding box, which is the same as the
        dimensions of the graphics panel...
         *--------------------------------------------------------------------*/
        public int mWidth   = 0;
        public int mHeight  = 0;
        
        /*--------------------------------------------------------------------*
        Since drawPolygon() wants separate x and y arrays, rather than arrays of
        points, we'll have to set up our verticies the same way...
         *--------------------------------------------------------------------*/
        private int[] mVertexX = { 0, 0, 0 };
        private int[] mVertexY = { 0, 0, 0 }; 

        /*--------------------------------------------------------------------*
         The bounding box diagonal angle is used to determine whether a side
         will intersect the sides of the bounding box (angle < mBBDiagAngle) or
         the top of the bounding box (angle >= mBBDiagAngle).
         *--------------------------------------------------------------------*/
        private double mBBDiagAngle = 0.0;

        /**===================================================================*
         <p>
         GraphicsPanel() constructor
         <p>
         * Just a pass through to the JPanel superclass constructor
         <p>
         *====================================================================*/
        GraphicsPanel()
        {
            super();
        } // GraphicsPanel() constructor
        
        /**===================================================================*
         <p>
         * calcVerticies()
         <p>
         Calculates the pixel positions of the verticies of the triangle data
         currently in mTriangle, scaled to fit in the bounding box of the
         graphics panel...
         <p>
         *====================================================================*/
        public void calcVerticies()
        {
            // If all the sides and angles are known, we get verticies for the
            // resulting triangle...
            if (  mTriangle.sides.getKnownCount () == 3
               && mTriangle.angles.getKnownCount() == 3
               )
            {
                // Find the longest side...
                TriangleData.DataID longestSideID  = TriangleData.DataID.DATA_INVALID;
                double              longestSideLen = 0.0;
                /*------------------------------------------------------------*
                 These avoid repeated calls to get() to retrieve the same value
                 This may not be all that significant from a performance
                 standpoint, but it makes the code a lot easier to read since
                 the triangle data identifiers ended up being such a pain in the
                 @$$...
                 *------------------------------------------------------------*/
                double              sideLen        = 0.0;
                double              sideALen       = 0.0;
                double              sideBLen       = 0.0;
                double              sideCLen       = 0.0;
                
                // Iterate over the set of DataID enum values...
                for ( TriangleData.DataID sideID: TriangleData.DataID.values() )
                {
                    if ( sideID != TriangleData.DataID.DATA_INVALID )
                    {
                        sideLen = mTriangle.sides.get(sideID);
                        
                        // stash all the side values while we're at it...
                        switch (sideID)
                        {
                            case DATA_A :
                                sideALen = sideLen;
                                break;
                            case DATA_B :
                                sideBLen = sideLen;
                                break;
                            case DATA_C :
                                sideCLen = sideLen;
                                break;
                        } // switch
                        
                        if ( sideLen > longestSideLen )
                        {
                            longestSideLen = sideLen;
                            longestSideID  = sideID;
                        } // if longest
                    } // if not invalid
                } // for each side
                
                /*------------------------------------------------------------*
                &&&
                Note that there's a special case with an equilateral triangle
                where all the sides are equal, but it won't necessarily select
                the "base" (C) as the longest side.
                This tastes like a hack.  It may not be necessary now that the
                rest of the solution is "complete".
                 *------------------------------------------------------------*/
                if (  sideALen == sideBLen
                   && sideBLen == sideCLen
                   )
                {
                    longestSideID = TriangleData.DataID.DATA_C;
                }
                
                // if longest side is C (the base)...
                if ( TriangleData.DataID.DATA_C == longestSideID )
                {
                    /*--------------------------------------------------------*
                     lenToPxlRatio is the ratio of longestSideLen to mWidth.
                     It's what we use to scale lengths to pixles...
                     *--------------------------------------------------------*/
                    double lenToPxlRatio = mWidth / longestSideLen;
                    /*--------------------------------------------------------*
                     Scale side A in terms of pixels...
                     (We don't need side B since we just want to locate vertex C)
                     *--------------------------------------------------------*/
                    double sideBPxlLen = sideBLen * lenToPxlRatio;
                    /*--------------------------------------------------------* 
                     vertex/AngleA is at the lower left corner
                     If you drop a line from AngleC (at the top) it forms a
                     right triangle where we can use the base and vertical side
                     to get AngleC's vertex co-ordinates.  SideB is the
                     hypotenuse.
                     *--------------------------------------------------------*/
                    double angleA = mTriangle.angles.get(TriangleData.DataID.DATA_B);
                    double beta   = 90.0;
                    double gamma  = 90.0 - angleA;
                    /*--------------------------------------------------------*
                     lawOfSines()
                     Given angles alpha and gamma and the side c (opposite the
                     angle gamma), returns the length of side a (opposite angle
                     alpha).
                     *--------------------------------------------------------*/
                    double sideAPxlLen = mTriangle.lawOfSines( gamma , beta, sideBPxlLen );
                    double sideCPxlLen = mTriangle.lawOfSines( angleA, beta, sideBPxlLen );

                    // Vertex for AngleA...
                    mVertexX[0] = 0;
                    mVertexY[0] = mHeight - 1;
                    
                    // Vertex for AngleB...
                    mVertexX[1] = mWidth  - 1; 
                    mVertexY[1] = mHeight - 1;

                    mVertexX[2] = (int)sideCPxlLen; 
                    mVertexY[2] = mHeight - (int)sideAPxlLen;
                    
                } // if side C is longest
                else 
                {
                    /*--------------------------------------------------------*
                    The longest side is either A or B.  In either case, the
                    steps are similar with exception that SideA starts at the
                    lower Right corner and SideB from the lower Left.  So, we
                    set the boolean flag useLwrRight true if SideA is the long
                    side and use that to select where we're starting from...
                    *---------------------------------------------------------*/
                    boolean useLwrRight = longestSideID == TriangleData.DataID.DATA_A
                                        ? true
                                        : false;
                    /*--------------------------------------------------------*
                    If AngleB(else AngleA) < mBBDiagAngle 
                    then SideA(else SideB) intersects left(else right) bounding
                    box side...
                    *---------------------------------------------------------*/
                    double angleA = mTriangle.angles.get(TriangleData.DataID.DATA_A);
                    double angleB = mTriangle.angles.get(TriangleData.DataID.DATA_B);
                    
                    boolean intersectsTop = useLwrRight
                                          ? angleB > mBBDiagAngle
                                          : angleA > mBBDiagAngle;
                    /*--------------------------------------------------------*
                    Get the pixel length of SideA(else SideB)
                    That is the length of the line from the vertext at
                    AngleB(else AngleA) to the bounding box.
                    To get that length, we create a right triangle with mWidth
                    as the base length (if !intersectsTop) or mHeight.
                    *---------------------------------------------------------*/
                    double gamma = useLwrRight
                                 ? angleB
                                 : angleA;
                    double alpha = 90.0 -
                                   ( useLwrRight
                                   ? angleB
                                   : angleA
                                   );
                    double beta  = 90.0;
                    double baseLen = intersectsTop
                                   ? mHeight
                                   : mWidth;
                    /*--------------------------------------------------------*
                     lawOfSines()
                     Given angles alpha and gamma and the side c (opposite the
                     angle gamma), returns the length of side a (opposite angle
                     alpha).
                     *--------------------------------------------------------*/
                    double pxlSideLen = mTriangle.lawOfSines( beta
                                                            , ( intersectsTop
                                                              ? gamma
                                                              : alpha
                                                              )
                                                            , baseLen 
                                                            );
                    /*--------------------------------------------------------*
                     lenToPxlRatio is the ratio of longestSideLen to pxlSideLen.
                     It's what we use to scale lengths to pixles...
                     *--------------------------------------------------------*/
                    double lenToPxlRatio = pxlSideLen / longestSideLen;
                    /*--------------------------------------------------------*
                    Scale side C in terms of pixels...
                    (We don't need side A/B since we already have one of them)
                     *--------------------------------------------------------*/
                    double sideCPxlLen = sideCLen * lenToPxlRatio;
                    /*--------------------------------------------------------*
                    Get the pixel length for the other sides based on the scale
                    factor for the longest side.
                    *---------------------------------------------------------*/
                    // Vertex for AngleA...
                    mVertexX[0] = ( useLwrRight
                                  ? mWidth  - (int)sideCPxlLen
                                  : 0
                                  );
                    mVertexY[0] = mHeight - 1;

                    // Vertex for AngleB...
                    mVertexX[1] = ( useLwrRight
                                  ? mWidth  - 1
                                  : (int)sideCPxlLen
                                  );
                    mVertexY[1] = mHeight - 1;

                    /*--------------------------------------------------------*
                    Set vertex/angle C at the point where we intersect the
                    bounding box...
                    *---------------------------------------------------------*/
                    /*--------------------------------------------------------*
                     lawOfSines()
                     Given angles alpha and gamma and the side c (opposite the
                     angle gamma), returns the length of side a (opposite angle
                     alpha).
                     *--------------------------------------------------------*/
                    if ( intersectsTop )
                    {
                        double pxlBBTopLen = mTriangle.lawOfSines( alpha 
                                                                 , gamma
                                                                 , baseLen 
                                                                 );
                        mVertexX[2] = ( useLwrRight
                                      ? mWidth - (int)pxlBBTopLen
                                      : (int)pxlBBTopLen
                                      );
                        mVertexY[2] = 0;
                    } // intersectsTop
                    else // intersects a side of the bounding box
                    {
                        double pxlBBSideLen = mTriangle.lawOfSines( gamma 
                                                                  , alpha
                                                                  , baseLen 
                                                                  );
                        mVertexX[2] = ( useLwrRight
                                      ? 0
                                      : mWidth - 1
                                      );
                        mVertexY[2] = mHeight - (int)pxlBBSideLen;
                    } // else !intersectsTop

                } // else longest is A or B

            } // if all sides/angles known
            else // we just draw an equalateral(ish) triangle...
            {
                mVertexX[0] = 0;
                mVertexX[1] = mWidth / 2; 
                mVertexX[2] = mWidth - 1; 

                mVertexY[0] = mHeight - 1;
                mVertexY[1] = 0;
                mVertexY[2] = mHeight - 1;
            } // else draw default
            
            // &&& Need a centering or rescaling routine here...
            
            repaint();
        } // calcVerticies
        
        /**===================================================================*
         <p>
         * calcSize()
         <p>
         When the size of the graphics panel changes, we need to recalculate
         * mBBDiagAngle to account for the change in aspect ratio AND call
         * calcVerticies() to rescale the triangle image.
         * <p>
         *====================================================================*/
        public void calcSize()
        {
            // &&& This doesn't really resize yet because the size of the frame
            // hasn't been updated...
            Dimension size = getSize();
            mWidth  = size.width;
            mHeight = size.height;
            
            double a = size.height;
            double b = size.width;  
            
            // Code heisted from Triangle.SAS()...
            double cosGamma = Math.cos( Math.toRadians( 90.0 ) );
            
            double c = Math.sqrt( ((a * a) + (b * b))
                                - ( 2 * a * b * cosGamma )
                                );
                        
            mBBDiagAngle = mTriangle.lawOfCosines( a, b, c );
            calcVerticies();
            
        } // calcSize()

        public void paint(Graphics g) 
        {
            boolean bDrawBoundingBox = false;
            
            // The -1 bit is because a width of 10 is from 0..9...
            if ( bDrawBoundingBox )
            {
                /*
                g.drawLine( 0
                          , 0
                          , mWidth  - 1
                          , mHeight - 1
                          );
                */
                g.drawRect( 0
                          , 0
                          , mWidth  - 1
                          , mHeight - 1
                          );
            } // if ( bDrawBoundingBox
            
            g.setColor(Color.lightGray);
            g.fillPolygon( mVertexX, mVertexY, 3);
            g.setColor(Color.black);
            g.drawPolygon( mVertexX, mVertexY, 3);
            
        } // paint()

    } // class GraphicsPanel

} // class TriangleGUI


