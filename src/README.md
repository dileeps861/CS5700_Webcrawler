Webcrawler which crawls through the website fakebook and looks for the secrets hidden inside the fakebook pages.

#### Design

The design approach I used is to divide the problem in different segments/ methods.

1. To read and send the data to host and keep reading crawledResponse from the server.
2. To crawl through the website and find the secret flags, got all the links which are from fakebook and using bfs find
   until all the pages are explored.
3. Create several classes to modularize the code and make it more readable and understandable.
4. For example created a class called `ResponseParser` which has all the methods to crawl through the a response and
   find the links, secret flags, and other important data.

#### Problems Faced

1. Make file structure was difficult to understand as now I have several classes. After reading documents, and trying
   out several things, could get it done.
2. Making http request and handling cookies with socket programming was challenging and had to read documentations of
   HTTP to understand responses.

#### Testing

To test if my program is working fine. I wrote unit test cases to check if parsing is done properly and able to redirect
to next page. Also finding the secret flag parse works fine. Ran my program on the server and checked if it is able to
find all the secret flags and able to run the program within a limited time
