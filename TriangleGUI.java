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
import javax.swing.*;  //notice javax

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
    
    Triangle  mTriangle = null;
    
    /*========================================================================*
     Base Panel data...
     *========================================================================*/
    protected final int mFrameWidth  = 500;
    protected final int mFrameHeight = 400;
    
    // &&& Figure out how to get this from the system
    protected int screenWidth  = 1920;
    protected int screenHeight = 1200;
    
    // Center the frame on the screen...
    protected int frameX      = ( (screenWidth  / 2) 
                                - (mFrameWidth  / 2)
                                );
    protected int frameY      = ( (screenHeight / 2) 
                                - (mFrameHeight / 2)
                                );;
    
    JPanel  mBasePanel    = null; // holds everything
      
    /*========================================================================*
     Button Panel data...
     *========================================================================*/
    JPanel  mButtonPanel  = null; // holds buttons
    
    JButton mCalcButton   = null;
    JLabel  mCalcLabel    = null;
    
    JButton mResetButton  = null;
    JLabel  mResetLabel   = null;
    
    private int mButtonPanelHeight = 0;

    /*========================================================================*
     Data Panel data...
     *========================================================================*/
    JPanel              mDataPanel  = null; // holds data input fields
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
    
    final int    INDX_SID_A  = 0;
    final int    INDX_SID_B  = 1;
    final int    INDX_SID_C  = 2;

    final int    INDX_ANG_A  = 0;
    final int    INDX_ANG_B  = 1;
    final int    INDX_ANG_C  = 2;


    
    /*========================================================================*
     Graphic Panel data...
     *========================================================================*/
    JPanel  mGraphicPanel = null; // holds triangle graphic
    
    /*========================================================================*
     TriangleGUI() constructor
     *=========================================================================*/
    TriangleGUI
    ( Triangle aTriangle
    )
    {
        super("Triangle Solutions GUI");
        
        mTriangle = aTriangle;
        
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
    
     Sets up the data button...
    
     Since this takes up all the vertical space that the button panel *doesn't*
     take, we need to create the button panel *first* in order to get the value
     of 
    
     *=========================================================================*/
    private JPanel createDataPanel()
    {
        final int dataPanelWidth  = mFrameWidth;
        final int dataPanelHeight = mFrameHeight - mButtonPanelHeight;
        
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
        JFormattedTextField field = new JFormattedTextField( new Double( 0.0 ) );
        
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
        mResetButton.setLocation( calcButtonX + buttonWidth + horzPad // reset to right of calc
                                , vertPad / 2 
                                );
        
        buttonPanel.add(mResetLabel);
        buttonPanel.add(mResetButton);

        // buttonPanel.setOpaque(true);
        
        return buttonPanel;
        
    } // createButtonPanel()
    
    /*========================================================================*
     roundDouble()
     *========================================================================*/
    private double roundDouble
    ( double aDouble
    )
    {
        final int    NUM_DECIMAL_PLACES = 4;
        final double FACTOR             = Math.pow(10, NUM_DECIMAL_PLACES);
        double    result                = aDouble;
        
        result = Math.round( result * FACTOR) / FACTOR;
        
        return result;
        
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
            // &&& This needs to...
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
            /*
            JOptionPane.showMessageDialog( null
                                         , "Reset all to zero"
                                         , "Message"
                                         , JOptionPane.PLAIN_MESSAGE
                                         );
            setVisible(true);  // show something
            */
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

} // class TriangleGUI


