package com.hardin.wilson.jobs;

import java.io.PrintWriter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hardin.wilson.app.NeighborhoodContainer;
import com.hardin.wilson.pojo.Descriptions;

public class NeighborhoodDescriptionJob extends ProcessingJob {
    
    public static final String URL_PREFIX = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles=";
    public static final String URL_SUFFIX = ",_Seattle";
    public static final String DELIM = "extract";
    public static final String OUTPUT_FILE = "data/neighborhood_descriptions.json";
    
    public void run() {
        System.out.println("Fetching descriptions..");
        ObjectMapper mapper = new ObjectMapper();
        Descriptions desc = new Descriptions();
        // add descriptions that need to be put in manually
        desc.addDescription("High Point", "High Point is a neighborhood in the Delridge district of West Seattle, Washington, USA. It is so named because it contains the highest point in Seattle; the intersection of 35th Avenue SW and SW Myrtle Street is 520 feet (160 m) above sea level. The neighborhood is located on the east side of 35th Ave SW, with approximate north and south boundaries at SW Juneau Street and SW Myrtle Street. The hill is dominated by two huge water towers, and is also the location of Our Lady of Guadalupe School and Parish, on the peak of the highest hill in West Seattle. It is also known for the High Point Projects which were torn down in 2005 to make way for new mixed income housing. High Point is one of Seattle's most diverse neighborhoods, with a substantial immigrant population from Southeast Asia and East Africa.", "http://en.wikipedia.org/wiki/West_Seattle,_Seattle");
        desc.addDescription("Highland Park", "Highland Park is traditionally a working-class neighborhood, due to its proximity to Boeing Field and other employers in the Industrial District. As with White Center immediately to the south, it now features wide demographic and ethnic diversity. Westcrest Park is adjacent to the West Seattle Reservoir. It offers an off-leash dog area.", "http://en.wikipedia.org/wiki/Delridge,_Seattle");
        desc.addDescription("Loyal Heights", "Loyal Heights was first considered a suburb of the city of Ballard, before it was annexed into Seattle. It includes the area north of 65th and west of 15th Avenue, extending to Puget Sound, though sometimes the western part is referred to as Sunset Hill. The man who first developed the area had a daughter named Loyal. He must have loved her a lot, because he not only named the neighborhood after her, but also Loyal Beach and the local trolley line. (As a side note, you have to wonder how is other daughter, Priscilla, felt about all of that!) Nearly all of the residences here are homes that were built before the 1960s, which only adds to the old-school charm of this neighborhood.", "http://seattle.findwell.com/seattle-neighborhoods/loyal-heights/");
        desc.addDescription("North Beach", "With expansive views of Puget Sound, one of Seattle’s best parks—Carkeek—in its back yard, and beautiful homes that were almost all built within twenty years of each other, the Blue Ridge neighborhood of north Seattle has plenty to offer. Just south of Blue Ridge and bordering another stunner of a park, Golden Gardens, North Beach is a little less formal than Blue Ridge, but has similar homes and views. The two neighborhoods are often lumped together as the Blue Ridge/North Beach neighborhood.", "http://seattle.findwell.com/seattle-neighborhoods/blue-ridge-north-beach/");
        desc.addDescription("North College Park", "Seattle annexed most of North Seattle in 1954. North College Park became defined with the Licton Springs neighborhood with the establishment of North Seattle Community College (1970). Facing NW. Soon after 2006 renovation Licton Springs and the Sunny Walter–Pillings Pond are part of the Densmore Drainage Basin. The springs at the North Police Precinct and North Seattle Community College are headwaters of the south fork of Thornton Creek; this fork flows through culverts under I-5 and the south lot of Northgate Mall development. These neighborhoods are natural extensions of Maple Leaf downstream. Neighborhood activists and North Seattle Community College (NSCC) have been promoting habitat restoration in support. NSCC grounds have a nationally-recognized native habitat, a pentimento of restored native species on a palimpsest of former 1940s suburb, former dairy farm, former bog where native Dkhw’Duw’Absh harvested cranberries.", "http://en.wikipedia.org/wiki/Licton_Springs,_Seattle#North_College_Park");
        desc.addDescription("Riverview", "South Seattle Community College (1970) is in Riverview. The college is notable for the South Seattle Community College Arboretum. The Seattle Chinese Garden borders the Arboretum. The gardens are on the bluff overlooking the Duwamish River. Boundaries include Puget Park/Oregon Avenue on the north, Delridge Avenue SW on the west, Highland Park Way SW, West Marginal Way SW and the Duwamish Waterway on the east, and SW Orchard Street, Dumar Way SW and SW Holden Street on the south", "http://en.wikipedia.org/wiki/Delridge,_Seattle#Riverview");
        desc.addDescription("Roxhill", "Roxhill is mostly residential areas, with a focal point of retail commerce but a great place to raise a family, if your financial situation allows for it. Westwood Village in Roxhill is a strip-mall which includes many retail standbys like Marshalls and Target, as well as strip mall necessities like Barnes & Noble. The real value that Westwood Village brings to Roxhill is a place to shop that is close to many homes , as well as servicing many of West Seattle bus riders. Roxhill isn’t all about retail. There are a lot of great areas suitable for field sports, picnics, and other outdoor activities. These green areas are interconnected with others throughout the city, which add to a certain community spirit. Also, Longfellow Creek flows through Roxhill Park which allows for relaxing evenings in the park.", "http://en.wikipedia.org/wiki/Delridge,_Seattle#Roxhill");
        desc.addDescription("Whittier Heights", "This small residential section of Ballard is ideal for people who love the vibe of northwest Seattle but want a slightly quieter place to call home. Much like other nearby subdivisions like Loyal Heights and Sunset Hill, Whittier Heights was only moved into once the housing options in downtown Ballard had filled up. The biggest era for growth was 1920 through 1940, when most of the houses were built. In more recent years, some new homes have been built, leaving the neighborhood with a nice mix of the vintage and the modern.", "http://seattle.findwell.com/seattle-neighborhoods/whittier-heights/");
        desc.addDescription("Olympic Manor", "A small neighborhood north of Ballard", "");
        for (String name : NeighborhoodContainer.getContainer().getNames()) {
            // skip manually entered neighborhoods
            if (desc.getDescriptions().containsKey(name))
                continue;
            String title = name.replace(" ", "_") + URL_SUFFIX;
            switch (name) { // fix names that don't match up with wikipedia
                case ("Alki"):
                    title = "Alki_Point" + URL_SUFFIX;
                    break;
                case ("Admiral"):
                    title = "North_Admiral" + URL_SUFFIX;
                    break;
                case ("Downtown"):
                    title = "Downtown_Seattle";
                    break;
                case ("International District"):
                    title = "Seattle_Chinatown-International_District";
                    break;
                case ("North Delridge"):
                    title = "Delridge" + URL_SUFFIX;
                    break;
                case ("Portage Bay"):
                    title = "Portage_Bay";
                    break;
                case ("South Delridge"):
                    title = "Delridge" + URL_SUFFIX;
                    break;
                
                    
                    
            }
            String url = URL_PREFIX + title;
            String response = ProcessingJob.fetchResource(url);
            if (response.contains(DELIM)) {
                String text = response.split(DELIM)[1];
                text = text.substring(3); // remove quotes at beginning
                text = text.split("\\\\n")[0]; // first paragraph only
                while (!text.endsWith(".")) {
                    text = text.substring(0, text.length() - 1);
                }
                text = text.replace("\\", "");
                String citation = "http://en.wikipedia.org/wiki/" + title;
                if (text.length() > 10) {
                    desc.addDescription(name, text, citation);
                }
                else
                    System.out.println("Error fetching description for " + name);
            } else {
                System.out.println("Error fetching description for " + name);
            }
        }
        try {
            String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(desc);
            PrintWriter out = new PrintWriter(OUTPUT_FILE);
            out.write(jsonString);
            out.close();
            System.out.println("Successfully fetched neighborhood descriptions");
        } catch (Exception e) {
            System.err.println("Error writing JSON to file: " + e.getMessage());
            System.err.println(e.getStackTrace());
            return;
        }
    }

}
