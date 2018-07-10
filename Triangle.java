/*============================================================================*
 Triangle.java
 
 Given the values of three sides and/or angles, this solves for the remaining
 unknown sides and/or angles.

 Notes:

 * Data representation...

    I came at this with C based idea that an enum was just an integer which 
    could then be used to index arrays and be simply incremented and decremented.
    This is apparently NOT the case in Java.

    Somewhat related to that was the idea that the Sides and Angles classes
    would each contain all three sides/angles.  It may well be that, the way
    Java enums work, it might have been a better idea to have a Side class just 
    represent A side.  Then again, it would then make less sense to have the
    class contain functions to traverse next and previous.

 * Accepted conventional triangle notation...

    In my tiny mind, it would make sense to indicate IDs for angles and sides
    in increasing order in the clockwise direction.  This is apparently NOT the
    case.  I tried to ignore this, but then things became confusing.  So, the
    ideas of Next and Previous STILL are in the clockwise direction, but that
    means that letter IDs are traversed in the OPPOSITE direction...so...
    starting from Angle A (angles being upper case, sides being lower case)...
    the sequence would be...

    (...c) A b C a B c (A...)

    You're going to see that in a lot of places because I needed to remind
    myself a lot.

 *============================================================================*/

import java.lang.*;
// import org.json.simple.JSONObject;

/*========================================================================*
 Triangle class
 *========================================================================*/
public class Triangle 
{
    public  Sides   sides   = new Sides ();
    public  Angles  angles  = new Angles();
    private boolean verbose = true;
    private boolean useFindSolution = false;
    
    // &&& Prolly need an explicit constructor
   
    /*========================================================================*
     main()
     This prolly needs to move out of the Triangle class
     *========================================================================*/
    public static void main(String[] args) 
    {
        boolean success  = false;

        Triangle triObj = new Triangle();
        // triObj.setFromCmdLine( args );
        
        TriangleGUI triGUI = new TriangleGUI( triObj );
        
        if (triObj.useFindSolution)
        {
            /*
            JSONObject data = new JSONObject
                { "angleA":[  0.0,  0.0,  0.0, 37.0 ]
                , "sideC" :[ 51.0,  0.0, 51.0,  0.0 ]
                , "angleB":[  0.0, 52.0, 54.0, 54.0 ]
                , "sideA" :[ 31.0, 30.0, 31.0, 31.0 ]
                , "angleC":[  0.0, 87.0,  0.0,  0.0 ]
                , "sideB" :[ 41.0,  0.0,  0.0,  0.0 ]                    
                };
            */
            /*----------------------------------------------------------------*
             &&&
             I guess I need something to either read the angle and side
             parameters from a file and cycle through them or just have the
             data in a hardcoded array.
             *----------------------------------------------------------------*/
            System.out.printf( "Call to findSolution() with SSS...\n");
            triObj.setAll( 0.0, 51.0, 0.0, 31.0, 0.0, 41.0 );
            success = triObj.findSolution();
            
            System.out.printf( "Call to findSolution() with ASA...\n");
            triObj.setAll( 0.0, 0.0, 52.0, 30.0, 87.0, 0.0 );
            success = triObj.findSolution();
            
            System.out.printf( "Call to findSolution() with SAS...\n");
            triObj.setAll( 0.0, 51.0, 54.0, 31.0, 0.0, 0.0 );
            success = triObj.findSolution();

            System.out.printf( "Call to findSolution() with AAS...\n");
            triObj.setAll( 37.0, 0.0, 54.0, 31.0, 0.0, 0.0 );
            success = triObj.findSolution();
        }
        else // brute force test code that runs all solutions...
        {
            // triObj.testAll();
        }

    } // main()
   
    /*========================================================================*
     setFromCmdLine()
    
     Description: 
        Populates triangle object with parameters from the command line.
    
     Returns:
        void.
     Note:

    *=========================================================================*/
    public void setFromCmdLine(String[] args)
    {
        int     argCount = args.length;
       
        if ( argCount >= 1 ) sides.set ( TriangleData.DataID.DATA_A, Double.parseDouble(args[0]) );
        if ( argCount >= 2 ) sides.set ( TriangleData.DataID.DATA_B, Double.parseDouble(args[1]) );
        if ( argCount >= 3 ) sides.set ( TriangleData.DataID.DATA_C, Double.parseDouble(args[2]) );

        if ( argCount >= 4 ) angles.set( TriangleData.DataID.DATA_A, Double.parseDouble(args[3]) );
        if ( argCount >= 5 ) angles.set( TriangleData.DataID.DATA_B, Double.parseDouble(args[4]) );
        if ( argCount >= 6 ) angles.set( TriangleData.DataID.DATA_C, Double.parseDouble(args[5]) );
       
        if ( verbose )
        {
            System.out.printf( "setFromCmdLine() captured the following parameters...\n");
            sides.print();
            angles.print();
        } // if verbose
           
    } // setFromCmdLine()
   
    /*========================================================================*
     setAll()
    
     Description: 
        Populates triangle object with passed parameters.
    
        Note that parameters start from angle A and traverse the triangle in the
        counter-clockwise direction.  The ordering was chosen to match up with
        the graphic I found on the web, starting with the lower left hand corner.
    
     Returns:
        void.
     Note:

    *=========================================================================*/
    public void setAll
    ( double angleA
    , double sideC
    , double angleB
    , double sideA
    , double angleC
    , double sideB
    )
    {
        sides.set ( TriangleData.DataID.DATA_A, sideA );
        sides.set ( TriangleData.DataID.DATA_B, sideB );
        sides.set ( TriangleData.DataID.DATA_C, sideC );

        angles.set( TriangleData.DataID.DATA_A, angleA );
        angles.set( TriangleData.DataID.DATA_B, angleB );
        angles.set( TriangleData.DataID.DATA_C, angleC );
       
        if ( verbose )
        {
            System.out.printf( "setAll() captured the following parameters...\n");
            sides.print();
            angles.print();
        } // if verbose
           
    } // setAll()
   
    /*========================================================================*
     testAll()
    
     Description: 
        Runs every available solution method with whatever data is currently in
        the triangle object.
     Returns:
        void (we're not keeping track of success)
     Note:

    *=========================================================================*/
    public void testAll()
    {
        if (!SSS())
        {
            System.out.printf( "No SSS solution found \n" );
        }

        System.out.printf( "Data after call to SSS()...\n");
        sides.print();
        angles.print();

        if (!SAS( TriangleData.DataID.DATA_C ))
        {
            System.out.printf( "No SAS solution found for %s \n"
                             , TriangleData.DataID.DATA_C.name()
                             );
        }

        System.out.printf( "Data after call to SAS()...\n");
        sides.print();
        angles.print();

        if (!ASA( TriangleData.DataID.DATA_C ))
        {
            System.out.printf( "No ASA solution found for %s \n"
                             , TriangleData.DataID.DATA_C.name()
                             );
        }

        System.out.printf( "Data after call to ASA()...\n");
        sides.print();
        angles.print();

        if (!AAS( TriangleData.DataID.DATA_C // angle C
                , TriangleData.DataID.DATA_B // angle B
                , TriangleData.DataID.DATA_C // side  c
                )
           )
        {
            System.out.printf( "No AAS solution found for %s \n"
                             , TriangleData.DataID.DATA_C.name()
                             );
        }

        System.out.printf( "Data after call to AAS()...\n");
        sides.print();
        angles.print();
       
    } // testAll()
   
    /*========================================================================*
     findSolution
    
     Description: 
        * Searches to see what sides and angles are non-zero
        * Determines what method should be used to find the knowns and calls
          it.
     Returns:
        true if successful.
        false if no solution found.
     Note:

    *=========================================================================*/
    public boolean findSolution()
    {
        boolean success     = false;
        int     knownSides  = sides.getKnownCount();
        int     knownAngles = angles.getKnownCount();
        
        if ( knownSides == 3 )
        {
            if ( SSS() )
            {
                success = true;
            }
            else
            {
                System.out.printf( "SSS solution failed.  Trying something else.\n" );
            }
        } // if 3 known sides
        else if( knownSides == 0 )
        {
            // Without any sides there are no possible solutions...
           System.out.printf( "No sides provided.  No solution possible.\n" );
           return success;
        } // elseif no known sides
        else if (  knownAngles == 0
                && knownSides  <  3
                )
        {
            // Without any angles and less than 3 sides we're also screwed...
           System.out.printf( "No angles provided.  No solution possible.\n" );
           return success;
        } // elseif no known angles
        
        TriangleData.DataID sideID  = TriangleData.DataID.DATA_INVALID;
        TriangleData.DataID angleID = TriangleData.DataID.DATA_INVALID;
        
        // At this point, we know we have 1..3 angles and 1..2 sides
        // First check 2 angle solutions
        if ( !success
           && knownAngles == 2 
           )
        {
            sideID = findDataASA();

            if ( sideID != TriangleData.DataID.DATA_INVALID )
            {
                // If known side between known angles
                if ( !( success = ASA( sideID ) ) )
                {
                    System.out.printf( "No ASA solution found for %s \n"
                                     , sideID.name()
                                     );
                    /*--------------------------------------------------------*
                     &&&
                     If ASA fails with supposedly good data, does it make sense
                     to try other approaches?
                     *--------------------------------------------------------*/
                } // if ASA bombs
            } // if there's data for ASA somewhere
            
            // If nothing in ASA worked...
            if ( !success )
            {
                angleID = findDataAAS();
                
                if ( angleID != TriangleData.DataID.DATA_INVALID )
                {
                    // If known side between known angles
                    if ( !( success = AAS( angleID
                                         , TriangleData.getNextDataID( angleID )
                                         , angleID // unintuitive, I know...
                                         // angleID when used as a sideID represents
                                         // the side opposite the angle, which is
                                         // what we want here.
                                         ) 
                          ) 
                       )
                    {
                        System.out.printf( "No AAS solution found for %s \n"
                                         , angleID.name()
                                         );
                        /*----------------------------------------------------*
                         &&&
                         If AAS fails with supposedly good data, does it make
                         sense to try other approaches?
                         *----------------------------------------------------*/
                    } // if AAS bombs
                } // if there's data for AAS somewhere
            } // if still no solution
        } // if no prev success and 2 angles

        if ( !success )
        {
            // At this point we only have one angle and 1..2 sides (SAS())
            if ( knownSides == 2 )
            {
                angleID = findDataSAS();

                // If knownAngles is one and we have two adjacent sides then SAS
                // SAS();
                if ( angleID != TriangleData.DataID.DATA_INVALID )
                {
                    // If known angle between two known sides
                    // &&& Fix AAS() parameters
                    if ( !( success = SAS( angleID ) ) )
                    {
                        System.out.printf( "No SAS solution found for %s \n"
                                         , angleID.name()
                                         );
                    } // if SAS bombs
                } // if there's data for SAS somewhere
            } // if no prev success and 2 sides
            else
            {
                // With only one angle and one side we're also screwed...
               System.out.printf( "Only one angle and one side provided.  No solution possible.\n" );
               return success;

            } // else only one angle and side
        } // if solution not found yet 

        sides.print();
        angles.print();
       
        return success;
        
    } // findSolution()

/*============================================================================*
    TRIG FUNCTIONS...
 *============================================================================*/
    /*========================================================================*
     lawOfCosines
    
     Description:
        Given sides a, b and c, calculate the angle of alpha (A), which is the
        angle opposite side a.

     Returns:
        Angle in degress.

     *========================================================================*/
    public double lawOfCosines
    ( double a
    , double b
    , double c
    )
    {
        double alpha = 0.0;
       
        if (  a >= 0.0
           && b >= 0.0
           && c >= 0.0
           )
        {
            alpha = Math.acos( ((b * b) + (c * c) - (a * a))
                             / (2 * b * c)
                             );
            alpha = Math.toDegrees( alpha );
        }
       
        return alpha;
       
    } // lawOfCosines()

    /*========================================================================*
     lawOfSines
    
     Description:
        Given angles alpha and gamma and the side c (opposite the angle gamma),
        returns the length of side a (opposite angle alpha).

     Returns:
        Length of side a.
     
    Note:
        If any input parameter is 0.0, the result will be 0.0.

     *========================================================================*/
    public double lawOfSines
    ( double alpha
    , double gamma
    , double c
    )
    {
        double a = 0.0;
       
        if (  alpha > 0.0
           && gamma > 0.0
           && c     > 0.0
           )
        {
            a = c * ( Math.sin( Math.toRadians( alpha ) )
                    / Math.sin( Math.toRadians( gamma ) )
                    );
        }
       
        return a;
       
    } // lawOfSines()

/*============================================================================*
    FIND DATA FUNCTIONS...
 *============================================================================*/
    /*========================================================================*
     findDataASA()
    
     Description:
        Searches through Triangle member data to find two known angles with an
        included known side.

     Returns:
        If ASA data is known, returns the DataID of the included side.
        If none found returns TriangleData.DataID.DATA_INVALID
     *========================================================================*/
    public TriangleData.DataID findDataASA()
    {
        TriangleData.DataID sideID = TriangleData.DataID.DATA_INVALID;
        
        for ( TriangleData.DataID dID: TriangleData.DataID.values() )
        {
            if ( dID != TriangleData.DataID.DATA_INVALID )
            {
                /*------------------------------------------------------------*
                 If there are known angles on either side of the known side then
                 we have data we can use...
                 *------------------------------------------------------------*/
                if (  sides.isKnown ( dID )
                   && angles.isKnown( TriangleData.getPrevDataID( dID ) )
                   && angles.isKnown( TriangleData.getNextDataID( dID ) )
                   )
                {
                    sideID = dID;
                    break;
                } // if known ASA data
            } // if dID not invalid
        } // for each side

        return sideID;
        
    } // findDataASA() 

    /*========================================================================*
     findDataAAS()
    
     Description:
        Searches through Triangle member data to find two known angles with a
        non-included known side.

     Returns:
        If AAS data is known, returns the DataID of the "first" angle.
        If none found returns TriangleData.DataID.DATA_INVALID
     *========================================================================*/
    public TriangleData.DataID findDataAAS()
    {
        TriangleData.DataID angleID = TriangleData.DataID.DATA_INVALID;
        
        /*--------------------------------------------------------------------*
         &&&
         It seems that, the way I have this mapped out, it would miss the case
         where we have data for SAA.  How do we deal with that?
         *--------------------------------------------------------------------*/
        for ( TriangleData.DataID dID: TriangleData.DataID.values() )
        {
            if ( dID != TriangleData.DataID.DATA_INVALID )
            {
                /*------------------------------------------------------------*
                 If there are two known angles on either side of the known side
                 then we have data we can use...
                 *------------------------------------------------------------*/
                if (  angles.isKnown( dID )
                   && angles.isKnown( TriangleData.getNextDataID( dID ) )
                   && sides.isKnown ( dID )
                   )
                {
                    angleID = dID;
                    break;
                } // if known AAS data
            } // if dID not invalid
        } // for each side
       
        return angleID;
        
    } // findDataAAS() 

    /*========================================================================*
     findDataSAS()
    
     Description:
        Searches through Triangle member data to find two known sides with a
        included known angle.

     Returns:
        If SAS data is known, returns the DataID of the included angle.
        If none found returns TriangleData.DataID.DATA_INVALID
     *========================================================================*/
    public TriangleData.DataID findDataSAS()
    {
        TriangleData.DataID angleID = TriangleData.DataID.DATA_INVALID;
        
        for ( TriangleData.DataID dID: TriangleData.DataID.values() )
        {
            if ( dID != TriangleData.DataID.DATA_INVALID )
            {
                /*------------------------------------------------------------*
                 If there are known sides on either side of the known angle then
                 we have data we can use...
                 *------------------------------------------------------------*/
                if (  angles.isKnown( dID )
                   && sides.isKnown ( TriangleData.getPrevDataID( dID ) )
                   && sides.isKnown ( TriangleData.getNextDataID( dID ) )
                   )
                {
                    angleID = dID;
                    break;
                } // if known ASA data
            } // if dID not invalid
        } // for each side

        return angleID;
        
    } // findDataSAS() 

/*============================================================================*
    TRIANGLE SOLUTION FUNCTIONS...
 *============================================================================*/
    /*========================================================================*
     SSS
    
     Description:
        Given three sides.	 
        Attempts to calculate the value of all angles assuming all sides are
        specified.

     Returns:
        true if successful.

     *========================================================================*/
    public boolean SSS()
    {
        boolean successful = false;
       
        if (  sides.isKnown( TriangleData.DataID.DATA_A )
           && sides.isKnown( TriangleData.DataID.DATA_B )
           && sides.isKnown( TriangleData.DataID.DATA_C )
           )
        {
            double a = sides.get( TriangleData.DataID.DATA_A );  
            double b = sides.get( TriangleData.DataID.DATA_B );  
            double c = sides.get( TriangleData.DataID.DATA_C );
            
           
            double alpha = lawOfCosines( a, b, c );
            double beta  = lawOfCosines( b, c, a );
            
            // If the sum of the two shortest sides is less than the longest
            // side, there's no way to close the triangle.  This should check
            // for that...
            if (  !Double.isNaN( alpha )
               && !Double.isNaN( beta  )
                )
            {
                double gamma = 180.0 - alpha - beta;

                angles.set( TriangleData.DataID.DATA_A, alpha );
                angles.set( TriangleData.DataID.DATA_B, beta  );
                angles.set( TriangleData.DataID.DATA_C, gamma );

                successful = true;
            } // if good results from lawOfCosines()
           
        } // if all sides known
        else
        {
            // &&& Test to see if the sum of the two shortest sides is less than 
            // the longest side in order to produce a more useful/accurate error
            // message.
            // &&& Need to idenfify the missing sides, if that's the problem
            System.out.printf( "Error: Insufficient side data for SSS()\n" );
        }
   
        return successful;
   
    } // SSS()

    /*========================================================================*
     SAS
    
     Description: 
        Given two sides and the included angle.
        Attempts to calculate the value of the remaining two angles and
        remaining side.
        The input is the angle ID that adjacent to the two known sides.
    
     Returns:
        true if successful.

     *========================================================================*/
    public boolean SAS
    ( TriangleData.DataID includedAngle // input
    )
    {
        boolean successful = false;
        TriangleData.DataID prevSide = TriangleData.getPrevDataID( includedAngle );
        TriangleData.DataID nextSide = TriangleData.getNextDataID( includedAngle );
       
        if (  angles.isKnown( includedAngle )
           && sides.isKnown( TriangleData.getPrevDataID( includedAngle ) )
           && sides.isKnown( TriangleData.getNextDataID( includedAngle ) )
           )
        {
            /*----------------------------------------------------------------*
             Note in all the local variables that the assumption is made that
             we're starting with C (gamma) as the known angle.  That's merely
             to reference SOMETHING in order to enter things in a known order
             to make them match up with published forumlas.
             What those variables ultimately get assigned to is another matter.
             *----------------------------------------------------------------*/
            double gamma = angles.get( includedAngle );
            double a     = sides.get( nextSide );  
            double b     = sides.get( prevSide );
            
            double cosGamma = Math.cos( Math.toRadians( gamma ) );
            
            double c = Math.sqrt( ((a * a) + (b * b))
                                - ( 2 * a * b * cosGamma )
                                );
                        
            double alpha = lawOfCosines( a, b, c );

            double beta = 180.0 - alpha - gamma;
            
            if ( verbose )
            {
                System.out.printf( "Internal SAS() results...%n" );
                System.out.printf( "Side a = %f%n", a );
                System.out.printf( "Side b = %f%n", b );
                System.out.printf( "Side c = %f%n", c );
                System.out.printf( "Angle alpha = %f%n", alpha ); 
                System.out.printf( "Angle beta  = %f%n", beta  ); 
                System.out.printf( "Angle gamma = %f%n", gamma ); 
            } // if verbose  
           
            /*---------------------------------------------------------------*
             We've calculated values relative to a "local" gamma as the included
             angle, we now need to translate them to their absolute positional
             values...
             *---------------------------------------------------------------*/
            angles.setPrev( includedAngle, alpha );
            angles.setNext( includedAngle, beta  );

            /*---------------------------------------------------------------*
             Since we were provided the next and previous sides, we need to
             set the remaining side, so next next should do it...
             *---------------------------------------------------------------*/
            sides.setNext( TriangleData.getNextDataID( includedAngle ), c );
           
            successful = true;
           
        } // if SAS known
        else
        {
            // &&& Need to idenfify the missing sides and/or angle
            System.out.printf( "Error: Insufficient side and/or angle data for SAS()\n" );
        }
   
        return successful;
   
    } // SAS()

    /*========================================================================*
     ASA
    
     Description: 
        Given two angles and the included side.
        Attempts to calculate the value of the remaining two sides and
        remaining angle.
        The input is the side ID that adjacent to the two known angles.
     Returns:
        true if successful.

     *========================================================================*/
    public boolean ASA
    ( TriangleData.DataID includedSide // input
    )
    {
        boolean successful = false;
        TriangleData.DataID prevAngle = TriangleData.getPrevDataID( includedSide );
        TriangleData.DataID nextAngle = TriangleData.getNextDataID( includedSide );
       
        if (  sides.isKnown ( includedSide )
           && angles.isKnown( prevAngle    )
           && angles.isKnown( nextAngle    )
           )
        {
            /*----------------------------------------------------------------*
             Note in all the local variables that the assumption is made that
             we're starting with alpha (A) and beta (B) as the known angles and
             c as the known side.
             *----------------------------------------------------------------*/
            double c     = sides.get ( includedSide );
            double alpha = angles.get( nextAngle    );
            double beta  = angles.get( prevAngle    );
            
            double gamma = 180.0 - alpha - beta;
            
            double a     = lawOfSines( alpha, gamma, c );
            double b     = lawOfSines( beta,  gamma, c );
            
            if ( verbose )
            {
                System.out.printf( "Internal ASA() results...%n" );
                System.out.printf( "prevAngle %s%n"   , prevAngle.name()    );
                System.out.printf( "includedSide %s%n", includedSide.name() );
                System.out.printf( "nextAngle %s%n"   , nextAngle.name()    );
                System.out.printf( "Side a = %f%n", a );
                System.out.printf( "Side b = %f%n", b );
                System.out.printf( "Side c = %f%n", c );
                System.out.printf( "Angle alpha = %f%n", alpha ); 
                System.out.printf( "Angle beta  = %f%n", beta  ); 
                System.out.printf( "Angle gamma = %f%n", gamma ); 
            } // if verbose
           
            /*----------------------------------------------------------------*
             We've calculated values relative to a "local" c as the included
             side, we now need to translate them to their absolute positional
             values...
                (...c) A b C a B c (A...)
             *----------------------------------------------------------------*/
            sides.setPrev( includedSide, a );
            sides.setNext( includedSide, b );

            /*----------------------------------------------------------------*
             Since we were provided the next and previous angles, we need to
             set the remaining angle.
             *----------------------------------------------------------------*/
            angles.setNext( TriangleData.getNextDataID( includedSide ), gamma );
           
            successful = true;
           
        } // if ASA known
        else
        {
            // &&& Need to idenfify the missing sides and/or angle
            System.out.printf( "Error: Insufficient side and/or angle data for ASA()\n" );
        }
   
        return successful;
   
    } // ASA()

    /*========================================================================*
     AAS
    
     Description: 
        Given two angles and a non-included side.
        Attempts to calculate the value of the remaining two sides and
        remaining angle.
        The input is the known side ID.
     Returns:
        true if successful.

     *========================================================================*/
    public boolean AAS
    ( TriangleData.DataID angleA // input
    , TriangleData.DataID angleB // input
    , TriangleData.DataID sideID // input
    )
    {
        boolean successful = false;
        
        if (  sides.isKnown ( sideID )
           && angles.isKnown( angleA )
           && angles.isKnown( angleB )
           )
        {
            /*----------------------------------------------------------------*
             Note in all the local variables that the assumption is made that
             we're starting with alpha (A) and beta (B) as the known angles and
             a as the known side.  Note that we don't do anything with the side
             data until we get into the ASA().
             *----------------------------------------------------------------*/
            double alpha = angles.get( angleA );
            double beta  = angles.get( angleB );
            
            double gamma = 180.0 - alpha - beta;
            
            /*----------------------------------------------------------------*
             We now set the prev angle in the counter clockwise direction
             relative to our local beta angle...
             *----------------------------------------------------------------*/
            angles.setPrev( angleB, gamma );
            
            if ( verbose )
            {
                System.out.printf( "Internal AAS() results...%n" );
                System.out.printf( "angleA %s%n", angleA.name() );
                System.out.printf( "angleB %s%n", angleB.name() );
                System.out.printf( "sideID %s%n", sideID.name() );
                System.out.printf( "Angle alpha = %f%n", alpha ); 
                System.out.printf( "Angle beta  = %f%n", beta  ); 
                System.out.printf( "Angle gamma = %f%n", gamma ); 
            } // if verbose

            /*----------------------------------------------------------------*
             Since we now have all the angles, we'll proceed with ASA()...
             *----------------------------------------------------------------*/
            successful = ASA( sideID );
           
        } // if AAS known
        else
        {
            // &&& Need to idenfify the missing sides and/or angle
            System.out.printf( "Error: Insufficient side and/or angle data for AAS()\n" );
        }
   
        return successful;
   
    } // AAS()
  
} // class Triangle
