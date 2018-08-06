/*============================================================================*
 TriangleGUI.java
 
 Some sort of GUI to enter and display triangle data.
 At some point it should include a graphic representation of a default inital
 triangle and the resulting triangle scaled to fit the space of the initial
 default.


 *============================================================================*/


import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics;
import javax.swing.*;
import javax.swing.text.*;

/*========================================================================*
 TriangleGUI class
 *========================================================================*/
public class TriangleGUI 
    extends    JFrame 
    implements ActionListener
{
    ////////////////////////////////////////////////////////////////////////////
    // Member variables...
    ////////////////////////////////////////////////////////////////////////////

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
    private JPanel  mButtonPanel  = null; // holds buttons
    
    private JButton mCalcButton   = null;
    private JLabel  mCalcLabel    = null;
    
    private JButton mResetButton  = null;
    private JLabel  mResetLabel   = null;
    
    protected int mButtonPanelHeight = 0;

    /*========================================================================*
     Data Panel data...
     *========================================================================*/
    protected Triangle mTriangle   = null;
    
    protected JPanel   mDataPanel  = null; // holds data input fields
    // &&& Need a class to contain...
    //  dataField
    //  label
    //  labelText
    //  xPos
    //  yPos
    //  angle/side
    JFormattedTextField mDataSideA   = null;
    JFormattedTextField mDataSideB   = null;
    JFormattedTextField mDataSideC   = null;
    JFormattedTextField mDataAngleA  = null;
    JFormattedTextField mDataAngleB  = null;
    JFormattedTextField mDataAngleC  = null;
    
    JLabel              mLabelSideA  = null;
    JLabel              mLabelSideB  = null;
    JLabel              mLabelSideC  = null;
    JLabel              mLabelAngleA = null;
    JLabel              mLabelAngleB = null;
    JLabel              mLabelAngleC = null;
    
    final String STR_SIDE_A  = "Side A";
    final String STR_SIDE_B  = "Side B";
    final String STR_SIDE_C  = "Side C";
    final String STR_ANGLE_A = "Angle A";
    final String STR_ANGLE_B = "Angle B";
    final String STR_ANGLE_C = "Angle C";
    
    /*------------------------------------------------------------------------*
     &&&
     There must be a better way to do this.  Java doen't seem to want to let you
     simply increment/decrement an enumerated type and use the result as an int
     index...
     *------------------------------------------------------------------------*/
    final int    INDX_SID_A  = 0;
    final int    INDX_SID_B  = 1;
    final int    INDX_SID_C  = 2;

    final int    INDX_ANG_A  = 0;
    final int    INDX_ANG_B  = 1;
    final int    INDX_ANG_C  = 2;
    
    /*========================================================================*
     Graphic Panel data...
     *========================================================================*/
    private GraphicsPanel  mGraphicPanel  = null; // holds triangle graphic
    
    /*========================================================================*
     TriangleGUI() constructor
     *=========================================================================*/
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
        
        mBasePanel = createContentPane();

        con.add(mBasePanel); // add the base panel to frame
        
        mButtonPanel.requestFocus();
        
        // buttonPanel.setVisible(true);
        setVisible(true); // display this frame
    } // TriangleGUI constructor
  
    /*========================================================================*
     createContentPane()
    
    
     *=========================================================================*/
    private JPanel createContentPane()
    {
        JPanel basePanel  = new JPanel();
        basePanel.setLayout(null);
        
        mButtonPanel = createButtonPanel();
        basePanel.add(mButtonPanel);

        mDataPanel   = createDataPanel();
        basePanel.add(mDataPanel);

        basePanel.setOpaque(true);
        
        return basePanel;

    } // createContentPane()
    
    /*========================================================================*
     createDataPanel()
    
     Sets up the data fields...
    
     Since this takes up all the vertical space that the button panel *doesn't*
     take, we need to create the button panel *first* in order to get the value
     of mButtonPanelHeight
    
     *=========================================================================*/
    private JPanel createDataPanel()
    {
        // &&& needs to be in resize()
        int dataPanelWidth  = mFrameWidth;
        int dataPanelHeight = mFrameHeight - mButtonPanelHeight;
        
        final int fieldWidth  = 70;
        final int fieldHeight = 30;
        final int topPadding  = 10;
        final int btmPadding  = topPadding;
        final int sidePadding = 20;
        final int topRowY     = topPadding;
        final int midRowY     =  ( ( dataPanelHeight - topPadding - btmPadding ) / 2 )
                                 - fieldHeight + topPadding;
        final int btmRowY     = dataPanelHeight - (2 * fieldHeight) - btmPadding;
        
        final int lftX        = sidePadding;
        final int midLftX     = ( dataPanelWidth / 4 )
                              - ( fieldWidth     / 2 );
        final int midX        = ( dataPanelWidth / 2 )
                              - ( fieldWidth     / 2 );
        final int midRgtX     = ( ( dataPanelWidth / 4 ) * 3 )
                              - ( fieldWidth     / 2 );
        final int rgtX        = ( dataPanelWidth - fieldWidth - sidePadding );
        
        JPanel dataPanel  = new JPanel();
        
        ////////////////////////////////////////////////////////////////////////
        // Create the panel to put the data fields on...
        ////////////////////////////////////////////////////////////////////////
        dataPanel.setLayout(null);
        dataPanel.setLocation( 0
                             , 0
                             );
        dataPanel.setSize( dataPanelWidth
                         , dataPanelHeight
                         );
        
        ////////////////////////////////////////////////////////////////////////
        // Create data fields...
        ////////////////////////////////////////////////////////////////////////
        /*--------------------------------------------------------------------*
                             AngleC
                    SideB              SideA
            AngleA           SideC             AngleB
         Create fields in the counter clockwise direction starting at AngleA...
         *--------------------------------------------------------------------*/
        mDataAngleA = createDataField
                     ( lftX
                     , btmRowY
                     , fieldWidth
                     , fieldHeight
                     );
        mLabelAngleA = createDataLabel
                     ( lftX
                     , btmRowY
                     , fieldWidth
                     , fieldHeight
                     , STR_ANGLE_A
                     );
        /*
        mDataAngleA.addPropertyChangeListener( STR_ANGLE_A
                                             , new FormattedTextFieldListener()
                                             );
        */
        dataPanel.add( mDataAngleA  );
        dataPanel.add( mLabelAngleA );
       
        mDataSideC = createDataField
                     ( midX
                     , btmRowY
                     , fieldWidth
                     , fieldHeight
                     );
        mLabelSideC = createDataLabel
                     ( midX
                     , btmRowY
                     , fieldWidth
                     , fieldHeight
                     , "Side C"
                     );
        
        dataPanel.add( mDataSideC  );
        dataPanel.add( mLabelSideC );
       
        mDataAngleB = createDataField
                     ( rgtX
                     , btmRowY
                     , fieldWidth
                     , fieldHeight
                     );
        mLabelAngleB = createDataLabel
                     ( rgtX
                     , btmRowY
                     , fieldWidth
                     , fieldHeight
                     , "Angle B"
                     );
        
        dataPanel.add( mDataAngleB  );
        dataPanel.add( mLabelAngleB );
       
        mDataSideA = createDataField
                     ( midRgtX
                     , midRowY
                     , fieldWidth
                     , fieldHeight
                     );
        mLabelSideA = createDataLabel
                     ( midRgtX
                     , midRowY
                     , fieldWidth
                     , fieldHeight
                     , "Side A"
                     );
        
        dataPanel.add( mDataSideA  );
        dataPanel.add( mLabelSideA );
       
        mDataAngleC = createDataField
                     ( midX
                     , topRowY
                     , fieldWidth
                     , fieldHeight
                     );
        mLabelAngleC = createDataLabel
                     ( midX
                     , topRowY
                     , fieldWidth
                     , fieldHeight
                     , "Angle C"
                     );
        
        dataPanel.add( mDataAngleC  );
        dataPanel.add( mLabelAngleC );
       
        mDataSideB = createDataField
                     ( midLftX
                     , midRowY
                     , fieldWidth
                     , fieldHeight
                     );
        mLabelSideB = createDataLabel
                     ( midLftX
                     , midRowY
                     , fieldWidth
                     , fieldHeight
                     , "Side B"
                     );
        
        dataPanel.add( mDataSideB  );
        dataPanel.add( mLabelSideB );
        
        mGraphicPanel = new GraphicsPanel();
        mGraphicPanel.setLayout(null);
        mGraphicPanel.setLocation( fieldWidth + sidePadding
                                 , topPadding + (2 * fieldHeight)
                                 );
        mGraphicPanel.setSize( dataPanelWidth
                             - (( fieldWidth + sidePadding ) * 2 )
                             , dataPanelHeight
                             - (4 * fieldHeight)
                             - topPadding
                             - btmPadding
                             );
        mGraphicPanel.setOpaque(false);
        
        dataPanel.add( mGraphicPanel );
        
        mGraphicPanel.resize();
       
        return dataPanel;
        
    } // createDataPanel()
    
    /*========================================================================*
     createDataField()
    
     Sets up a data field...
     
     This takes the same parameters with the came VALUES as the associated
     createDataLabel() call (except for the missing label text).
     
     The label goes on TOP of the field, so the field height and yPos values get
     fixed up here.
    
     *=========================================================================*/
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

  
    /*========================================================================*
     createDataLabel()
    
     Sets up a data label...
     
     This takes the same parameters with the came VALUES as the associated
     createDataField() call.
     
     The label goes on TOP of the field, so the height and yPos values get fixed
     up in the createDataField() call.
    
     *=========================================================================*/
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
  
    /*========================================================================*
     createButtonPanel()
    
     Sets up button panel...
    
     *=========================================================================*/
    private JPanel createButtonPanel()
    {
        final int vertPad           = 10; // above and below buttons
        final int horzPad           = 10; // between buttons

        final int buttonWidth       = 100;
        final int buttonHeight      = 30;

        final int buttonCount       = 2;
        final int buttonPanelWidth  = mFrameWidth;
        mButtonPanelHeight          = buttonHeight + vertPad;
        
        JPanel buttonPanel  = new JPanel();
        
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
        // &&& needs to be in resize()
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
        // &&& needs to be in resize()
        mResetButton.setLocation( calcButtonX + buttonWidth + horzPad // reset to right of calc
                                , vertPad / 2 
                                );
        
        buttonPanel.add(mResetLabel);
        buttonPanel.add(mResetButton);

        // buttonPanel.setOpaque(true);
        
        return buttonPanel;
        
    } // createButtonPanel()
    
    /*========================================================================*
     setUpFormats()
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
    
    /*========================================================================*
     roundDouble()
     
     The intent here is to round the result to sidestep floating point inaccuracy
     beyond the 13th decimal place.
     *========================================================================*/
    private double roundDouble
    ( double aDouble
    )
    {
        // return Math.round( aDouble * ROUND_FACTOR) / ROUND_FACTOR;
        return aDouble;
        
    } // roundDouble
  
    /*========================================================================*
     actionPerformed()
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
             *----------------------------------------------------------------*/
            mTriangle.angles.set( TriangleData.DataID.DATA_A 
                                , Double.parseDouble( mDataAngleA.getText() )
                                );
            mTriangle.angles.set( TriangleData.DataID.DATA_B 
                                , Double.parseDouble( mDataAngleA.getText() )
                                );
            mTriangle.angles.set( TriangleData.DataID.DATA_C 
                                , Double.parseDouble( mDataAngleA.getText() )
                                );
            mTriangle.sides.set ( TriangleData.DataID.DATA_A 
                                , Double.parseDouble( mDataSideA.getText() )
                                );
            mTriangle.sides.set ( TriangleData.DataID.DATA_B 
                                , Double.parseDouble( mDataSideB.getText() )
                                );
            mTriangle.sides.set ( TriangleData.DataID.DATA_C 
                                , Double.parseDouble( mDataSideC.getText() )
                                );
    
            /*----------------------------------------------------------------*
             Get the solution...
             *----------------------------------------------------------------*/
            boolean success = mTriangle.findSolution();
            
            /*----------------------------------------------------------------*
             Display the result...
             *----------------------------------------------------------------*/
            if ( success )
            {
                mDataAngleA.setText
                    ( String.valueOf
                        ( roundDouble
                            ( mTriangle.angles.get( TriangleData.DataID.DATA_A )
                            )
                        )
                    );
                mDataAngleB.setText
                    ( String.valueOf
                        ( roundDouble
                            ( mTriangle.angles.get( TriangleData.DataID.DATA_B )
                            )
                        )
                    );
                mDataAngleC.setText
                    ( String.valueOf
                        ( roundDouble
                            ( mTriangle.angles.get( TriangleData.DataID.DATA_C )
                            )
                        )
                    );
                mDataSideA.setText
                    ( String.valueOf
                        ( roundDouble
                            ( mTriangle.sides.get( TriangleData.DataID.DATA_A )
                            )
                        )
                    );
                mDataSideB.setText
                    ( String.valueOf
                        ( roundDouble
                            ( mTriangle.sides.get( TriangleData.DataID.DATA_B )
                            )
                        )
                    );
                mDataSideC.setText
                    ( String.valueOf
                        ( roundDouble
                            ( mTriangle.sides.get( TriangleData.DataID.DATA_C )
                            )
                        )
                    );
                
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
            mDataSideA.setValue ( new Double( 0.0 ) ); 
            mDataSideB.setValue ( new Double( 0.0 ) ); 
            mDataSideC.setValue ( new Double( 0.0 ) ); 
            mDataAngleA.setValue( new Double( 0.0 ) );
            mDataAngleB.setValue( new Double( 0.0 ) );
            mDataAngleC.setValue( new Double( 0.0 ) );
            
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

    /*========================================================================*
     class GraphicsPanel
    
     Panel that hosts the triagnle graphic embedded in the dataPanel (mDataPanel).
     
     We needed a subclass where we could override paint() and get stuff painted.
    
     *========================================================================*/
    protected class GraphicsPanel
        extends  JPanel
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
        private int[] mVertexX = { 0, 0, 0};
        private int[] mVertexY = { 0, 0, 0}; 

        /*--------------------------------------------------------------------*
         The bounding box diagonal angle is used to determine whether a side
         will intersect the sides of the bounding box (angle < mBBDiagAngle) or
         the top of the bounding box (angle >= mBBDiagAngle).
         *--------------------------------------------------------------------*/
        private double mBBDiagAngle = 0.0;

        /*====================================================================*
         GraphicsPanel() constructor
         *====================================================================*/
        GraphicsPanel()
        {
            super();
        } // GraphicsPanel() constructor
        
        /*====================================================================*
         calcVerticies()
        
         Calculates the pixel positions of the verticies of the triangle data
         currently in mTriangle, scaled to fit in the bounding box of the
         graphics panel...
         *====================================================================*/
        public void calcVerticies()
        {
            // If all the sides and angles are known, we get vericies for the
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

                    mVertexX[2] = (int)sideAPxlLen; 
                    mVertexY[2] = mHeight - (int)sideCPxlLen;
                    
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
        
        public void resize()
        {
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
            
        } // resize()

        public void paint(Graphics g) 
        {
            boolean bDrawBoundingBox = true;
            
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


