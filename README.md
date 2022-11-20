# Abercrombie AEM Developer Skill Assessment
1. Clone this repository.
2. Complete exercises below by creating/modifying code. You can architect the project how you like re: folder structure, how you name your files, etc. Please add comments at the start and end of the code (i.e. `***Begin Code - Candidate Name***` and `***END Code*****`). Use your best judgement as a developer.
3. Push the code to your own public Git repository, and send the link to your recruiter / rep.
4. Pretend your code is going into a `PRODUCTION` environment, or that you are writing a pull request for an established open source project. Do not rush these exercises or cut corners in the name of speed. We aren't interested in the code you can write under pressure; no one writes amazing code when they are rushing. This is your chance to show off. Write your best code.
5. This exercise is to be completed without coaching or other outside assistance. Obviously, you may feel free to use whatever online resources you like -- `StackOverflow` etc. -- but it is not acceptable to utilize other developers to help you finish this task.


## Exercise 1: Saving data into JCR

Create a form (re-using OOB component where possible) which has the following fields: First Name, Last Name, Age, Country and a Submit button.  Create a `JS clientlib` for your component and write a validation logic on submit. For validation, fetch the min and max age data from the node `/etc/age`. If the user entered age lies in between the min and max value, all the user details will be saved inside JCR (under `/var/anf-code-challenge`) otherwise, an error message will be displayed with following text `You are not eligible`.

<div style="width:400px">

![](images/Exercise-1.png)
</div>

### Acceptance Criteria:
1. Fetch node details from /etc/age having properties: `minAge` and `maxAge`.
2. When submit button is clicked, validate the age and save the user details on a node under `/var/anf-code-challenge` after successful validation.
3. Populate the dialog dropdown (country component) dynamically using `JSON` in DAM (JSON Path: `/content/dam/anf-code-challenge/exercise-1/countries.json`) and display the selected country on the page.

### Notes:
1. Please refer to `exercises/Exercise-1` folder and deploy `Exercise-1.zip` onto your `AEM 6.5.0`
2. Call `UserServlet.java` from your clientlib JS to perform the required validations.


##Response From Mithun Halder:


1. Country drop down option population:  Drop down of the dialog box of country component is populated by adding a datasource with sling:resourceType="anf-code-challenge/json" and path="/content/dam/anf-code-challenge/exercise-1/countries.json/jcr:content/renditions/original". This invokes the servlet com.anf.core.servlets.PopulateDropdown.java  where sling.servlet.resourceTypes=anf-code-challenge/json. This servlet reads the json stored in /content/dam/anf-code-challenge/exercise-1/countries.json/jcr:content/renditions/original and populates the dropdown. 
The value selected on the dialog box is being printed on the form on the page: http://localhost:4502/content/anf-code-challenge/us/en/test-form.html

2. Basic form validation ( validating that first name, last name and age values are required to submit the form) is done by checking the "required" checkbox on the "Constrains" tab of the Form text field dialog of the form. 
container.html of form container component of core-component is slightly extended by ANF form container by loading client library js file with category=anf-code-challenge.components.form.container.
container.html is also updated by picking the action url of the form externalServiceEndPointUrl property.
action="${properties.externalServiceEndPointUrl}"

When form is submitted, /apps/anf-code-challenge/components/form/container/clientlibs/js/formcontainer.js is invoked and this js makes an ajax call ( get method) to  action url (/bin/saveUserDetails) which is the value of sling.servlet.paths of UserServlet.java. 
The UserServlet first call validateAge(int age) method of ContentService interface. 

validateAge()  method in ContentServiceImpl.java gets the ResourceResolver object from ResolverUtil.java. Please note that ResolverUtil class uses system user : anfserviceuser to get the ResourceResolver from ResourceResolverFactory. The configuration of system user and its mapping with the anf bundle is done using ACS-common  API. The configurations are available at /anf-code-challenge.ui.apps/src/main/content/jcr_root/apps/anf-code-challenge/config
Once we get resourceResolver object, we can get necessary resource and property to get the max and min age and validate the age. 

Once validation is successful, UserServlet calls commitUserDetails() method of contentService  to store the data by creating a node with name as full name(first name appended by last name without space)under /var/anf-code-challenge. If two fullnames are same, it will make the node unique by adding ascending numbers. 

 

## Exercise 2: News Feed Component

Every news feed item displays the following attributes:
1.	Title
2.	Author
3.	Current date
4.	Text/Description
5.	Image

<div style="width:400px">

![](images/Exercise-2_1.png)
</div>

### Node structure:

<div style="width:500px">

![](images/Exercise-2_2.png)
</div>

### Acceptance Criteria:

1.	Create news feed component following Adobe’s best practices.
2.	Read the news data under `/var/commerce/products/anf-code-challenge/newsData` and display it in the component.
3.	Write Unit test cases (using any unit-tests library) for the back-end code with at least `80% coverage` and commit the coverage report.

### Notes:
1. Please refer to `exercises/Exercise-2` folder and deploy `Exercise-2.zip` onto your `AEM 6.5.0`

## Response from Mithun Halder:

Custom component "newsfeed" is created under /apps/anf-code-challenge/components/newsfeed. The dialog of the component opens with a text field where source oath of the news feed can be configured. Default path is set as "/var/commerce/products/anf-code-challenge". The model of the component "NewsFeedImpl" populates the list of NewsArticle object with corresponding values from properties of news item under /var/commerce/products/anf-code-challenge/newsData. 
 /apps/anf-code-challenge/components/newsfeed/newsfeed.html file finally displays the news details. 
 
 Unit test class NewsFeedImplTest.java is written to to unit test NewsFeedImpl class.

## Exercise 3: Query JCR

Fetch the first 10 pages under the path `/content/anf-code-challenge/us/en`  where property `anfCodeChallenge` exists under the page node.

<div style="width:500px">

![](images/Exercise-3.png)
</div>

### Acceptance Criteria:
1. Fetch the first 10 pages using any two: `XPath`, `JCR-SQL2`, or `the Query Builder API`.
2. Follow Adobe's best practices for better performance.

### Notes:
1. Please refer to `exercises/Exercise-3` folder and deploy `Exercise-3.zip` onto your `AEM 6.5.0`

## Response from Mithun Halder

SearchServiceImpl class contains two methods :
1. getPagesUsingQueryBuilder(): This is to fetch first 10 pages using query builder API. This method internally calls getPagesUsingQueryBuilder() method create the query using query builder api. 
Here is the query by QueryBuilder API: 
type=cq:Page
path=/content/anf-code-challenge/us/en
1_property=jcr:content/anfCodeChallenge
1_property.value=true
p.limit=10

2. getPagesUsingSQL2() : This method can be used to fetch pages using using SQL2 query. Here is the query: 
SELECT parent.* FROM [cq:Page] AS parent INNER JOIN [nt:base] AS child ON ISCHILDNODE(child,parent) WHERE ISDESCENDANTNODE(parent, '/content/anf-code-challenge/us/en') AND child.[anfCodeChallenge] = 'true'  
 I dont think we can limit the number of fetched pages using SQL2.
 
 I have setup a servlet "SearchServlet" that invokes either of the above tow methods based on the query parameter "searchType" in the url. 
 In order to call query builder API, launch `http://localhost:4502/bin/getFirst10Pages?searchType=querybuilder`
 In order to call SQL2 query, launch `http://localhost:4502/bin/getFirst10Pages?searchType=sql2`
 


## Exercise 4: Saving a property on page creation

Use your best knowledge to choose among any of the eventing mechanisms available in AEM to write in to the JCR whenever a new page is created under `/content/anf-code-challenge/us/en`

### Acceptance Criteria:
1. Create a page under `/content/anf-code-challenge/us/en`.
2. As soon as the page is created, a property `pageCreated: {Boolean}true` should be saved on it.

## Response from Mithun Halder

com.anf.core.listeners.ANFJobCreater class is created to listen to Page Event  of type Addition. This class adds the job properties to JobManager with the topic "anf/job". The com.anf.core.listeners.ANFJobConsumer class processes this job with topic "anf/job" and set the property "pageCreated" under proper node. This process guarantees that job will be retried till configured retry limit even it fails . 



 


