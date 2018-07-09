/*============================================================================*
 TriangleData
 
 Base class for sides and angles data.
 
 *============================================================================*/
public abstract class TriangleData
{
    protected double[] dataArray = new double[3];
    protected int      knownCount;
    
    // Identifier enumeration...
    public enum DataID
    { DATA_A
    , DATA_B
    , DATA_C
    , DATA_INVALID
    }
    
    /*------------------------------------------------------------------------*
     Zero denotes a value that's unknown, since a zero length side would be
     invalid.  An angle of zero would also yeild a poor triangle. 
     *------------------------------------------------------------------------*/
    public TriangleData()
    {
        knownCount = 0;
        /*--------------------------------------------------------------------*
         Input is validated by set.  
         Must be >= 0.0
         Is there a practical maximum?
         
         Can you throw an exception in a constructor?
         *--------------------------------------------------------------------*/
        set( DataID.DATA_A, 0.0 );
        set( DataID.DATA_B, 0.0 );
        set( DataID.DATA_C, 0.0 );

    } // Sides default constructor
    
    /*=======================================================================*
     set
     
     Description: 
        Sets a data value
    
     @param dID   // input data item ID
     @param value // input data value
    
     @return:
        Resulting value of specified data item.
    
     Note:
        If value is < 0, the value is not changed and the previously
        existing value is returned.
     *=======================================================================*/
    public abstract double set
    ( DataID dID   // input
    , double value // input
    );

    /*=======================================================================*
     setNext
     
     Description: 
        Sets the value of the next data item (in the clockwise direction) from
        the specified dataID.

     @param dID   // input data item ID
     @param value // input data value
    
     @return:
        Resulting value of next data item.
    
     Note:
        If value is < 0, the value is not changed and the previously
        existing value is returned.
    
        Also, see the "Accepted conventional triangle notation" note in the
        Triangle.java file comment header.  It's important.  It basically means
        that advancing from A in the clockwise direction means we're going...
        C B A
    
        The next/previous semantics is the same for both sides and angles, so
        this shouldn't need to be overridden.
     *=======================================================================*/
    public final double setNext
    ( DataID dID   // input
    , double value // input
    )
    {
        double result = 0.0;
        
        // &&& There must be a better way than this...
        switch ( dID )
        {
        case DATA_A:
            result = set( DataID.DATA_C, value );
            break;
        case DATA_B:
            result = set( DataID.DATA_A, value );
            break;
        case DATA_C:
            result = set( DataID.DATA_B, value );
            break;
       default:
          System.out.printf( "TriangleData.setNext() called with %d\n"
                           , dID.ordinal()
                           );           
        } // switch

        return result;
        
    } // setNext()

    /*=======================================================================*
     setPrev
     
     Description: 
        Sets the value of the prev data item (in the clockwise direction) from
        the specified data item.
    
     @param dID   // input data item ID
     @param value // input data value
    
     @return:
        Resulting value of previous data item.
    
     Note:
        If value is < 0, the value is not changed and the previously
        existing value is returned.
    
        Also, see the "Accepted conventional triangle notation" note in the
        Triangle.java file comment header.  It's important.  It basically means
        that going back from A in the counter=clockwise direction means we're
        going...
        A B C
     *=======================================================================*/
    public final double setPrev
    ( DataID dID   // input
    , double value // input
    )
    {
        double result = 0.0;
        
        // &&& There must be a better way than this...
        switch ( dID )
        {
        case DATA_A:
            result = set( DataID.DATA_B, value );
            break;
        case DATA_B:
            result = set( DataID.DATA_C, value );
            break;
        case DATA_C:
            result = set( DataID.DATA_A, value );
            break;
       default:
          System.out.printf( "TriangleData.setPrev() called with %d\n"
                           , dID.ordinal()
                           );           
        } // switch

        return result;
        
    } // setPrev()

    /*=======================================================================*
     get
     
     Description: 
        Gets the value of one data item
    
     @param dID // input data item ID
    
     @return:
        Value of specified data item.
     Note:
        A value of 0.0 is considered unknown.
     *=======================================================================*/
    public final double get
    ( DataID dID   // input
    )
    {
        return dataArray[dID.ordinal()];
    } // get()

    /*=======================================================================*
     getKnownCount

     Description: 
        Returns the number of known data items
    
     @return: number of known data items
     *=======================================================================*/
    public final int getKnownCount()
    {
        return knownCount;
    } // getKnownCount()
    
    /*=======================================================================*
     isKnown

     Description: 
        Returns the number of known data items
     
     @param dID // input data item ID
     
     @return: true if value of dID is known
     *=======================================================================*/
    public final boolean isKnown
    ( DataID dID  // input
    )
    {
        return dataArray[dID.ordinal()] > 0.0;
    } // isKnown
    
    /*=======================================================================*
     getNext/Prev/DataID
    
     First, see the "Accepted conventional triangle notation" note in the
     file comment header.  It's important.
    
    (...c) A b C a B c (A...)
    
     The following two functions give the next or previous identifier
     assuming that next is in the clockwise direction and prev is in the
     counterclockwise direction.
    
     Also note that this started life with separate ID's for angles and sides.
     Given a side ID, it would return the next/previous adjacent angle ID.  It
     turns out that the adjacency progression is the same whether it's a side
     or and angle.

     Note that all of this switch statement crap is due to the fact that Java
     enum's aren't simple integers that you can increment and decrement and
     wrap when you're at the end of the list.  There MUST be a more elegant
     way to make this work.	 
     *=======================================================================*/
    /*=======================================================================*
     getNextDataID

     Returns:
         The next adjacent data ID for the given data ID
         (...c) A b C a B c (A...)
     *=======================================================================*/
    public static DataID getNextDataID
    ( DataID dID  // input
    )
    {
        DataID outputID = DataID.DATA_INVALID;
       
        switch ( dID )
        {
        case DATA_A:
            outputID = DataID.DATA_B;
            break;
        case DATA_B:
            outputID = DataID.DATA_C;
            break;
        case DATA_C:
            outputID = DataID.DATA_A;
            break;
       default:
          System.out.printf( "getNextDataID() called with %d\n"
                           , dID.ordinal()
                           );           
        } // switch
       
        return outputID;
       
    } // getNextDataID()
   
    /*=======================================================================*
     getPrevDataID

     Returns:
         The previous adjacent data ID for the given data ID
         (...c) A b C a B c (A...)
     *=======================================================================*/
    public static DataID getPrevDataID
    ( DataID dID  // input
    )
    {
        DataID outputID = DataID.DATA_INVALID;
       
        switch ( dID )
        {
        case DATA_A:
            outputID = DataID.DATA_C;
            break;
        case DATA_B:
            outputID = DataID.DATA_A;
            break;
        case DATA_C:
            outputID = DataID.DATA_B;
            break;
       default:
          System.out.printf( "getPrevDataID() called with %d\n"
                           , dID.ordinal()
                           );           
        } // switch
       
        return outputID;
      
    } // getPrevAngleID()
    
    public void print()
    {
        for ( DataID dID: DataID.values() )
        {
            if ( dID != DataID.DATA_INVALID )
            {			
                System.out.printf( "Value  %s  = %f%n"
                                 , dID.name()
                                 , get(dID)
                                 );
            } // if
        } // for
    } // print()

}// class TriangleData