/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ham.citybuildersim;

/**
 *
 * @author Jerus
 */
public enum BuildingType {
    RESIDENTIAL,
    COMMERCIAL,
    INDUSTRIAL,
    CONSTRUCTION,

    /**
     * Processors that buy their input abroad and sell their output abroad.
     *
     * Separate from INDUSTRIAL because that category feeds the food market and
     * prices off its own cost base; a second industry sharing that cost pool
     * would change what a loaf of bread appears to cost. See
     * HeavyIndustryHandler.
     */
    HEAVY_INDUSTRY,

    ELECTRICITY,
    WATER
    
    
    
}
