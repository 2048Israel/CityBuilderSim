package ham.citybuildersim;

/**
 *
 * @author Jerus
 */
public class BuildingInstance {

    BuildingsTemplate template;
    int constructionProgress;
    boolean paused;
    boolean completed;

    public BuildingInstance(BuildingsTemplate template) {
        this.template = template;
        this.constructionProgress = constructionProgress;
        this.paused = false;
        this.completed = false;
    }

    public void advanceConstruction() {
        if (!paused && !completed) {
            constructionProgress++;
            if (constructionProgress >= template.constructionPoints) {
                completed = true;
                System.out.println(template.name + "construction finished");
            }
        }
    }
    
    //setters
    
    //getters
    public int getJobs(JobType type) {
        return template.getJobs(type);
    }
    public int getTotalJobs() {
        return template.getTotalJobs();
    }
}
