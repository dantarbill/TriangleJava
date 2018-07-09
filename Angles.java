/*============================================================================*
 Angles

 Container for triangle angle values.
 
 ...could be input and output in different units...
 * Degrees
 * Radians
 * For now let's not consider degrees, hours, minutes, seconds etc.
 
 *============================================================================*/
public class Angles extends TriangleData
{
    private final double ANG_360    = 360.0;

    /*------------------------------------------------------------------------*
     Zero denotes a value that's unknown, since a zero angle would be
     invalid. 
     *------------------------------------------------------------------------*/
    public Angles()
    {
        super();
        
    } // Angles() default constructor

    /*------------------------------------------------------------------------*
     Zero denotes a value that's unknown, since a zero angle would be
     invalid. 
     *------------------------------------------------------------------------*/
    public Angles
    ( double A
    , double B
    , double C
    )
    {
        knownCount = 0;
        /*--------------------------------------------------------------------*
         Input is validated by set.  
         Must be >= 0.0

         Can you throw an exception in a constructor?
         *--------------------------------------------------------------------*/
        set( DataID.DATA_A, A );
        set( DataID.DATA_B, B );
        set( DataID.DATA_C, C );

    } // Angles() constructor

    /*========================================================================*
     set

     Description: 
        Sets the value of an angle
     Returns:
        Resulting value of specified angle where angle is >= 0.0 and < 360.0.
     Note:
    If angle is < 0.0, the value is not changed and the previously
            existing value is returned.
            Is there a practical maximum?
     *========================================================================*/
    @Override
    public double set
    ( DataID angleID  // input
    , double angle    // input
    )
    {
        if (  angle > 0.0 
           && angle % ANG_360 != 0
           )
        {
            // Normalize if > 360
            if ( angle > ANG_360 )
            {
                Double divAngle = new Double( angle / ANG_360 );
                int multiplier  = divAngle.intValue();
                angle           = angle - ( multiplier * ANG_360 );
            }
        
            // if the existing value is zero, then we have a new angle with a value
            if ( dataArray[angleID.ordinal()] == 0.0 )
            {
                knownCount++;            
            }
            dataArray[angleID.ordinal()] = angle;
        }
        // If we're setting this to zero and it wasn't zero before, decrement
        // the knownCount
        else if (  (  angle == 0.0
                   || angle % ANG_360 != 0
                   )
                && dataArray[angleID.ordinal()] > 0.0
                )
        {
            knownCount--;
            dataArray[angleID.ordinal()] = angle;
        }

        return dataArray[angleID.ordinal()];

    } // set()

    @Override
    public void print()
    {
        System.out.printf( "Angles...\n" ); 
        super.print();
    } // print()

}// class Angles