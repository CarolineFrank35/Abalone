# Abalone

Useful Links

How to Program a Hexagonal Grid
http://www.quarkphysics.ca/scripsi/hexgrid/
Interactive Hexagonal Grid Explanation
https://www.redblobgames.com/grids/hexagons/

## Explanation
The documents that were in the root directory  
were now moved into the subfolder ./Documents/  

Source files are under ./src/main/java   
and test files are under ./src/test/java

### Tests
Tests will be performed utilizing JUnit 5 and AssertJ for various Assertions.     
A good Introduction to JUnit5: https://junit.org/junit5/docs/current/user-guide/#writing-tests    

Assertions provided by AssertJ: http://joel-costigliola.github.io/assertj/    

Tests will automatically be run when a commit is being merged with the *develop* or *master* branch    
Tag the last commit before creating a merge request with the tag `merge-<branch_name>-<version>`   
By doing this a test run will be triggered on the tagged commit so we have the information if a commit   
is in a passing state before applying the merge.    

Generally speaking every Controller class should be tested.    


### Dependencies and Building
Dependencies and building will be handled by Maven.   
Maven is already configured and should work out of the Box with all common   
IDEs.


### Workflow
* Pick and assign a task on Trello to yourself, then move it to "In Progress"
* Create a branch describing the feature that you are working on, branching **from** *develop*.
    * `git fetch` *fetches all changes done on the upstream server*   
     `git checkout develop` *moves to the **develop** branch*   
    `git pull` *pulls latest changes to the current branch from the server*   
    `git branch <branch_name>` *Where <branch_name> is the name of your feature branch*   
    `git checkout <branch_name>` *moves to the **branch_name** branch*   
    `git push -u origin <branch_name>` *The flag -u maps the new branch to the upstream server*   
    
 
* After changing code, add the changes to the staging area.   
`git add *`   
Then create a commit (a commit is like a checkpoint that you can always revert to)   
`git commit -m "<message>"` _(always add a descriptive commit message to the commit)_   
To reflect your changes to the upstream server you should **push** your changes   
`git push`   

 * When you are finished with your feature tag the last commit of your branch with the tag *merge-<branch_name>-<version\>*   
 `git tag merge-<branch_name>-<version>` *Tags are unique. That's why you need to provide a branch_name and a version*   
 e.g. `git tag merge-world_domination-1`   
 then push the tag    
 `git push origin merge-<branch_name>-1`   
 e.g. `git push merge-world_domination-1`   
 This will trigger a test run on the CI system.
 
 * Create a *Merge Request* from the Web UI of GitLab.   
 **(*Plus icon at the top right corner of the screen -> This Project-> New merge request*)**   
 Then select your branch as the source branch and **develop** as the target branch   
 then click "compare branches and continue".   
 In the merge request screen you will see if your last commit passed on the CI system (symbolized by a green tick mark)   
 When the build passed, click the "Merge button"