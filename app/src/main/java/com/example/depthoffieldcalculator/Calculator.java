package com.example.depthoffieldcalculator;

public class Calculator {

    public static double calcHyperfocalDistance(int focalLength, double selectedAperture, double coc){
        double hyperfocalDistance = (focalLength * focalLength) / (selectedAperture * coc);

        return hyperfocalDistance;
    }

    public static double calcNearFocalPoint(double hyperfocalPoint, double distance, int focalLength){
        double nearFocalPoint = (hyperfocalPoint * distance)
                / (hyperfocalPoint + distance - focalLength);

        return nearFocalPoint;
    }

    public static double calcFarFocalPoint(double hyperfocalPoint, double distance, int focalLength){
        double farFocalPoint;
        if (distance > hyperfocalPoint){
            farFocalPoint = Double.POSITIVE_INFINITY;
        }
        else{
            farFocalPoint = (hyperfocalPoint * distance)
                    / (hyperfocalPoint - (distance - focalLength));
        }

        return farFocalPoint;
    }

    public static double calcDepthOfField(double farFocalPoint, double nearFocalPoint){
        double depthOfField = farFocalPoint - nearFocalPoint;

        return depthOfField;
    }


}
