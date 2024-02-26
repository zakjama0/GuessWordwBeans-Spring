# Types of HTTP Request

## Learning Objectives

* Understand the difference between the different HTTP request methods
* Be able to determine an appropriate status code for a response
* Be able to use `ResponseEntity` to correctly format responses
* Be able to use `@RequestBody` to handle client input

## Introduction

In the last lesson we started building a guessing game where players will attempt to guess a mystery word one letter at a time. We ensured that our client could make a request to the API to start a new game (and fixed an issue where they immediately saw the answer) but so far that's all they can do.

To add more functionality we need to add more routes to our controller. It's not a simple matter of laying out more code though, if we don't think things through our app will get very messy very quickly. We can take steps to avoid this by following conventions such as REST and structuring our routes in an intuitive manner, but also by considering the *types* of request our API can handle.

## Making Requests with HTTP

Virtually all of us use HTTP many times every day without even thinking about it. It's how our web browsers request data and how many apps interact with their servers. Even if we know HTTP is involved we often don't know *how* it's being used by any given application. It can often seem like we're only using it to retrieve data, and when we're working in a web browser that's often what's happening. Browsers are very restrictive tools though, especially without adding some JavaScript to enhance the functionality of a web page. They intentionally limit the types of HTTP request a user can make. 

#### `GET` Requests

When we type a URL into a web browser we are actually telling it to make an HTTP `GET` request. `GET` requests are used whenever we want to retrieve some information from a resource without making any changes. When we annotated the method in our controller with `@GetMapping` we indicated to our API that it should be responsible for handling any incoming `GET` requests made to that route. 

Note that the method we annotated didn't take any arguments. In some cases a client can submit data as part of making a request, but `GET` requests do not permit this. It is possible to include extra details in the request by adapting the URL and this is a very common pattern used to filter the information being requested. For example, we might make a request to a restaurant's API to limit the menu options by making a `GET` request to `localrestaurant.com/menu?diet=vegan`. `GET` is generally considered to be a "safe" method, meaning requests made that way won't affect the data state of the application at all. Given that our API currently has a `GET` request creating a new game object, way may need to have a bit of a re-think.

#### `POST` Requests

The other type of request available to us through a web browser is the `POST` request. Whenever we click the "submit" button on an HTML `<form>` element its default behaviour is to send a `POST` request to a route defined according to the form's `action` attribute. `POST`s are used to create something on the server, with or without user input.

Unlike a `GET` request, a `POST` will allow a client to include some data within the body of the request. That data can be de-serialised by the server and used in the creation of a new resource but it isn't mandatory. We can augment the URL in the same way as for a `GET` request but when submitting complex data this is generally avoided. `POST`s are the polar opposite of `GET`s in that they should *always* result in something being created in the server. When working within a browser using only HTML `POST` has to fill in for many other responsibilities too, such as updating or deleting resources. As soon as we add JavaScript to a web app we open up the ability to make further request types, meaning our API will need to be able to handle them.

#### `PATCH` & `PUT` Requests

We can update an existing resource using a `POST` request to overwrite it with something new and get the job done but this isn't the usual way to do it. Instead we can use one of two requests to indicate that we are making an update, depending on how we are doing it.

The `PATCH` request is used when a resource only needs to be partially updated. For example, in our lessons example above we might use a `PATCH` request to change the name of the trainer delivering one of the lessons. The URL we make the request to should indicate which lesson is being updated while the new content should be included in the request's body. It is up to the API to determine how to handle the information received.

If we want to completely replace a resource we use a `PUT` request. It follows the same structure as a `PATCH` in that the URL specifies the resource and the body contains the new content. If we were to completely replace one of our lessons we would use a `PUT` to supply the new details.

#### `DELETE` Requests

The missing piece of the puzzle is how we remove something from our API. The `DELETE` request specifies which resource is to be removed in the URL and should not include a request body. This is similar to using the `rm` command in terminal in that once a resource is gone, it's gone for good.

## Status Codes

Once our API knows how to handle the different types of request it also needs to know how to respond to them correctly. A cornerstone of HTTP is the use of three-digit **status codes** in the responses to give a clear indication of what has happened to either a human user or another system. Recall that there are five categories of code, with the first digit indicating which one a code belongs to.

- `1XX - Informational` - These codes give the client information about how the request was handled
- `2XX - Success` - The request was handled successfully
- `3XX - Redirection` - The server redirected the request to a different route
- `4XX - Client Error` - Something was wrong with the request and the server wasn't able to process it properly
- `5XX - Server Error` - The request was correctly formed but an issue in the server prevented it being handled

Each category is sub-divided into individual codes. Some are widely used while others only have niche uses. Common examples include:

- `200 - OK` - Everything went fine
- `201 - Created` - A new resource has been created on the server
- `400 - Bad Request` - The request was badly formed in some way
- `404 - Not Found` - The server does not recognise the URL the request was made to
- `405 - Method Not Allowed` - The wrong type of request has been made to the URL
- `500 - Internal Server Error` - A run-time error has occurred while processing the request.

Our application already makes use of these status codes but does so in a fairly broad way. For example, any successful request to any route defined in the controller will get a `200` response. This at least tells us that nothing went wrong, but it doesn't give us as much information as it could. We may want to indicate that a resource has been created, for example, or use a `404` code if a search returns nothing. To give us that level of control we need to reconfigure our controller to structure our responses differently.

## Formatting an HTTP Response

At the moment we only have one route which accepts a `GET` request and returns a serialised `Game` object. As discussed above we will get a response code in the client, but at the moment we will only see one of three: `200` if it works, `500` if something goes wrong internally or `405` if we make a different type of request. We have no fine control over the status code to give more information.

There are a few different ways of adding this functionality to our responses and which one is appropriate for any given situation will depend on many factors such as how the controller is configured, how the content is being serialised and others. In our API we will use the `ResponseEntity` class to change all of our return types into something which supports these codes being assigned at instantiation.

`ResponseEntity` takes a generic parameter in the same way as `List`s or `Map`s. Instead of returning a `Reply` object we can state our return type as `ResponseEntity<Reply>` to indicate that a serialised `Reply` will form the body of the response. Instantiating the object requires two arguments: a `Reply` POJO and the status code, represented in Spring Web as an enum. 

```java title="controllers/GameController.java"
// controllers/GameController.java

@GetMapping
public ResponseEntity<Reply> newGame(){
 	Game game = new Game("hello");
   	Reply reply = new Reply("*****", "New game started");
   	return new ResponseEntity<>(reply, HttpStatus.OK);
}

```

We see no change in the result on the client side, meaning the reply is still being created and serialised as it should. Things aren't quite right yet though. A new `Game` object is being created when we make our request but the status code isn't giving us any information to confirm that, it simply says "OK". Now we can configure our status code we should do so to show that a new resource has been created.

```java title="controllers/GameController.java"
// controllers/GameController.java

@GetMapping
public ResponseEntity<Reply> newGame(){
 	Game game = new Game("hello");
   	Reply reply = new Reply("*****", "New game started");
   	return new ResponseEntity<>(reply, HttpStatus.CREATED);		// MODIFIED
}

```

Now when we make a request our client receives a `201` status code indicating that a new game object has been created. We're still not there though...

## Handling Requests Correctly

Recall that a `GET` request should return information to the client but should **never** modify the application's state. The response from the server when we make a `GET` request, though, is telling us that something *is* changing. There's an inconsistency there and something needs to change.

We definitely want to create a new `Game` object when we start a new game, so the status code is correct. We need to modify our code to associate the method with a different type of request. THe only thing we need to change is the annotation, updating it to use `@PostMapping`.

```java title="controllers/GameController.java"
// controllers/GameController.java

@PostMapping									// MODIFIED
public ResponseEntity<Reply> newGame(){
 	Game game = new Game("hello");
   	Reply reply = new Reply("*****", "New game started");
   	return new ResponseEntity<>(reply, HttpStatus.CREATED);
}

```

Rerunning our query will now give us a `405` status code in response since our controller no longer knows how to handle `GET` request to `localhost:8080/games`. If we modify our request to use the `POST` method, however, we get the same reply POJO as before with a `201` status. Note that we can no longer make our request using a browser's address bar as these are configured to only make `GET` requests. To test our API from now on we need to use tools such as [Postman](https://www.postman.com/) or [Insomnia](https://insomnia.rest/).

It's likely we will want to find out how the game is going so we still need to have a `GET` request available to retrieve the information. That will require a refactoring of our existing method since the game object is currently scoped inside the `newGame` method, so nothing else can access it. We need to make it a property of the controller, along with the current state of the player's progress. While we're adding properties we'll also add a list of strings to keep track of previous guesses.

```java title="controllers/GameController.java"
// controllers/GameController.java

@RestController
@RequestMapping(value = "/games")
public class GameController {

   	private Game game;		// ADDED
   	private String currentWord;	// ADDED
   	private ArrayList<String> guessedLetters;	// ADDED
    
   	@PostMapping
   	public ResponseEntity<Reply> newGame(){
      	this.game = new Game("hello");		// MODIFIED
      	this.currentWord = "*****";		// ADDED
      	this.guessedLetters = new ArrayList<>();	// ADDED
      	Reply reply = new Reply(currentWord, "New game started");
      	return new ResponseEntity<>(reply, HttpStatus.CREATED);
   	}

}
```

The `game` and `currentWord` properties are now accessible from anywhere in the controller, which will be necessary as we add more routes. We'll start by making our `GET` request to see what the current state of play is. 

```java title="controllers/GameController.java"
// controllers/GameController.java

@RestController
@RequestMapping(value = "/games")
public class GameController {

   // ...
   
   @GetMapping
  	public ResponseEntity<Reply> getGameStatus(){
     	Reply reply = new Reply(currentWord, "Game in progress.");
      	return new ResponseEntity<>(reply, HttpStatus.OK);
   	}

}
```

Note that we make the request to the same route. We will follow the [REST](https://restfulapi.net/) convention as far as possible when designing our APIS which encourages reusing routes with the requests differentiated by the method used. It is the controller's job to examine the request, determine which method was used and handle it appropriately.

We can start a game and see how it's going but we still can't *play* the game. To do that we need to learn how to include some information in our requests.

## Handling Input from the Client

We have seen how to include JSON in the responses from our API, now it's time to include some in the requests as well. We will still be communicating using JSON but this time instead of serialising a POJO to send we need to de-serialise some JSON into a POJO usable by Spring.

To handle that process we need a class to define the structure of those POJOs. Our client is going to send JSON describing a guess they make so we will create a `Guess` model. It only needs one property, a `String` to represent the guessed letter.

```java title="models/Guess.java"
// models/Guess.java

public class Guess {

   	private String letter;

   	public Guess(String letter) {
      	this.letter = letter;
   	}

   	public Guess() {
   
   }

   	public String getLetter() {
      	return letter;
   	}

   	public void setLetter(String letter) {
      	this.letter = letter;
  	}
}
``` 

The getter and setter are still necessary. As with the `Reply` object in the previous lesson, if any are missing or have mis-matching types we won't be able to correctly de-serialise the JSON. When the client submits a request its body should include JSON with the keys matching the properties of `Guess`.

```json
{
	"letter": "a"
}
```

Our controller needs to be able to handle such a request, first by using the correct method annotation. When a player makes a guess we will be updating the `currentWord` variable, although if a player guesses incorrectly it may not change at all. Since we are working with a `String` object here and we will replace it with a new `String`, albeit one derived from the previous value, we could argue for either a `PATCH` or `PUT` method here. We will use `PATCH` since we may wish to change the representation of our data at some point in the future.

```java title="controllers/GameController.java"
// controllers/GameController.java

@PatchMapping
public ResponseEntity<Reply> handleGuess(){
        
}
```

The `handleGuess` method will need to take a `Guess` POJO as a parameter in order to make use of it, but how do we know to look inside the request to find it? We will include the `@RequestBody` annotation in the parameter list to show that it will come from the request's body.

```java title="controllers/GameController.java"
// controllers/GameController.java

@PatchMapping
public ResponseEntity<Reply> handleGuess(@RequestBody Guess guess){
        
}
```

It is critical that the JSON keys correspond to the properties of `Guess` and the values have the appropriate data types. If they do not the server will respond with a `500` error as something will be `null` in the POJO when it is de-serialised. 

Once we have de-serialised the JSON we can treat it just like any other object and incorporate it into our code. We will add functionality to the route to check the guess against the game's `word` property and update `currentWord` to show which letters have been guessed. First we need to modify `Reply` to include a property showing if the guess was correct or not.

```java title="models/Reply.java"
// models/Reply.java

public class Reply {

   	private String wordState;
   	private String message;
   	private boolean correct;

   	public Reply(String wordState, String message, boolean correct) {
      	this.wordState = wordState;
      	this.message = message;
      	this.correct = correct;
   	}

	// add getter and setter
}

```

The `handleGuess` method will have the guess-checking logic added:

```java title="controllers/GameController.java"
// controllers/GameController.java

@PatchMapping
public ResponseEntity<Reply> handleGuess(@RequestBody Guess guess) {
  	// create new Reply object
   	Reply reply;

    // Check if game has started 
    if (this.game == null) {
        reply = new Reply(
            this.currentWord,
            String.format("Game has not been started"),
            false);
        return new ResponseEntity<>(reply, HttpStatus.OK);
    }

   	// check if letter has already been guessed
   	if (this.guessedLetters.contains(guess.getLetter())) {
   		reply = new Reply(
      		this.currentWord,
   			String.format("Already guessed %s", guess.getLetter()
   		), false);
   		return new ResponseEntity<>(reply, HttpStatus.BAD_REQUEST);
  	}
   	
   	// add letter to previous guesses
  	this.guessedLetters.add(guess.getLetter());
   	
   	// check for incorrect guess
  	if (!game.getWord().contains(guess.getLetter())) {
    	reply = new Reply(
        	this.currentWord,
         	String.format("%s is not in the word", guess.getLetter()),
         	false
      	);
      return new ResponseEntity<>(reply, HttpStatus.OK);
   	}
        
  	// process correct guess
  	String runningResult = game.getWord();

  	for (Character letter : game.getWord().toCharArray()) {
    	if (!this.guessedLetters.contains(letter.toString())) {
        	runningResult = runningResult.replace(letter, '*');
      	}
   	}
   	
   	this.currentWord = runningResult;

  	reply = new Reply(
      	this.currentWord,
      	String.format("%s is in the word", guess.getLetter()),
      	true)
  	);

  	// return result
  	return new ResponseEntity<>(reply, HttpStatus.OK);
}
```

Now we can finally play our game! If we make a guess:

```json
{
	"letter": "h"
}
```

We get a response telling us if we were successful or not:

```json
{
   	"correct": true,
   	"wordState": "h****",
   	"message": "h is in the word"
}
```

We have made significant steps towards having a fully-functional game, although there are still some gaps in the logic. Before we add in the missing pieces, though, we need to take a serious look at the structure of our code. It might work, but it's violating the Single Responsibility Principle in a big way. Our controller *should* be responsible for determining what to do with incoming requests and sending out responses, with each of it methods handling a single request. Instead it has properties hanging around describing things which aren't related to routing and the methods include some fairly complex game logic. We need to move some code out of here and into a separate classes where we can actually play the game.
