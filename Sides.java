/*============================================================================*
 Sides
 
 Container for triangle side values.
 
 *============================================================================*/

public class Sides extends TriangleData
{
    public Sides()
    {
        super();
    } // Sides default constructor
    
    /*=======================================================================*
     set
     
     Description: 
        Sets a data value
    
     Returns:
        Resulting value of specified data value.
    
     Note:
        If value is < 0, the value is not changed and the previously
        existing value is returned.
     *=======================================================================*/
    @Override
    public double set
    ( DataID sideID // input
    , double length // input
    )
    {
        if ( length > 0.0 )
        {
            // if the existing value is zero, then we have a new non-zero value
            if ( dataArray[sideID.ordinal()] == 0.0 )
            {
                knownCount++;            
            }
            dataArray[sideID.ordinal()] = length;
        }
        // If we're setting this to zero and it wasn't zero before, decrement
        // the knownCount
        else if (  length == 0.0
                && dataArray[sideID.ordinal()] > 0.0
                )
        {
            knownCount--;
            dataArray[sideID.ordinal()] = length;
        }
        
        return dataArray[sideID.ordinal()];
        
    } // set()

    @Override
    public void print()
    {
        System.out.printf( "Sides...\n" ); 
        super.print();
    } // print()

}// class Sides