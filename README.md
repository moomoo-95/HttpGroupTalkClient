# HttpGroupTalk

## Protocol
### HGTP (Http Group Talk Protocol)
#### Define : Session management protocol for http communication between multiple users.
#### Format : Server & Client, request & response
#### Request
REGISTER : Request to register a client from the server.<br>
UNREGISTER : Request to unregister a client from the server.<br>
CREATE_ROOM : Request to create a room.<br>
DELETE_ROOM : Request to delete a room.<br>
JOIN_ROOM : Request to enter the room.<br>
EXIT_ROOM : Request to exit the room.<br>
INVITE_USER_FROM_ROOM : Request to invite another user to the current room. (only manager)<br>
REMOVE_USER_FROM_ROOM : Request to kick another user out of the current room. (only manager)<br>
#### Response
OK : Response to a successfully processed request.<br>
BAD_REQUEST : Response to request failure by the user.<br>
UNAUTHORIZED : Response for register authentication.<br>
FORBIDDEN : Response due to register authentication failure.<br>
SERVER_UNAVAILABLE : Response to request failure by the server.<br>
DECLINE : Response to request failure by the peer user.<br>

#### Header Format (24 bytes)
![image](https://user-images.githubusercontent.com/58906637/149041844-be3f4340-9d65-47fa-8486-e2751aeb9db7.png)
#### Content (Variable)
Include the data needed for each request and response.
## Structure
### Basic Flow
![image](https://user-images.githubusercontent.com/58906637/148345567-485d7f5c-7715-4e30-b910-41c0aab6182b.png)

### HGTP Invite/Remove Flow
![image](https://user-images.githubusercontent.com/58906637/148345677-048b3f75-f7b9-4018-ba14-e84d72c80117.png)

### HGTP Create/Delete/Join/Exit Flow
![image](https://user-images.githubusercontent.com/58906637/148345916-181046da-dfa8-41eb-a675-ab8af7ca05e0.png)

### HGTP Register/Unregister
![image](https://user-images.githubusercontent.com/58906637/148346095-abf9f359-c80c-4877-a3b7-7b9b1fde7563.png)

### HTTP Flow
![image](https://user-images.githubusercontent.com/58906637/148345776-9dfa9e0b-b581-4e55-ab47-8a8b20da1ea6.png)

### Transport Structure
![image](https://user-images.githubusercontent.com/58906637/152719176-5331cc49-51c1-4176-a47e-b70c7c964e4e.png)