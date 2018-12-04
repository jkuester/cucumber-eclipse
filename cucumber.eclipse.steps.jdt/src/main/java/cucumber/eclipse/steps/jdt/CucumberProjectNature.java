package cucumber.eclipse.steps.jdt;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;

public class CucumberProjectNature implements IProjectNature {
	
	public static final String CUCUMBER_NATURE = "cucumber.eclipse.steps.jdt.stepsNature";
	public static final String CUCUMBER_NATURE_MISSING_MARKER = "cucumber.eclipse.markers.project.cucumber_nature_missing";
	
    private IProject project;
    
    public void configure() throws CoreException {
        addBuilder(project);
    }

    public void deconfigure() throws CoreException {
    	removeBuilder(project);
    }

    public IProject getProject() {
        return project;
    }

    public void setProject(IProject project) {
        this.project = project;
    }

    private void addBuilder(IProject project) throws CoreException {
        IProjectDescription description = project.getDescription();
        
        // Avoid using a Set since ICommand does not have 
        // a hash + equalTo methods to avoid duplicates
        // So we will filter by id
        Map<String, ICommand> builders = new HashMap<String, ICommand>(description.getBuildSpec().length + 1);
        for (ICommand builder : description.getBuildSpec()) {
			builders.put(builder.getBuilderName(), builder);
		}
        
        ICommand detectStepDefinitionsBuilder = description.newCommand();
        detectStepDefinitionsBuilder.setBuilderName(StepsBuilder.BUILDER_ID);
        builders.put(detectStepDefinitionsBuilder.getBuilderName(), detectStepDefinitionsBuilder);
        
        description.setBuildSpec(builders.values().toArray(new ICommand[builders.size()]));
        project.setDescription(description, new NullProgressMonitor());
    }
    
    private void removeBuilder(IProject project) throws CoreException {
    	IProjectDescription description = project.getDescription();
    	Set<ICommand> builders = new HashSet<ICommand>(Arrays.asList(description.getBuildSpec()));

    	Set<ICommand> toRemove = new HashSet<ICommand>();
        for (ICommand builder : builders) {
			if(StepsBuilder.BUILDER_ID.equals(builder.getBuilderName())) {
				toRemove.remove(builder);
			}
		}
        builders.removeAll(toRemove);
        
        description.setBuildSpec(builders.toArray(new ICommand[builders.size()]));
        
        project.setDescription(description, new NullProgressMonitor());
    }
}
