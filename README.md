# Coco Cinema  

## Demo Video:  
* A demo video can be found [here](https://www.youtube.com/watch?v=PxzKoiAHjDQ) 
* In the video, the Android application was installed at Huawei P30 Pro (screen size: 6.47", resolution: 1080*2340px)
* Note that because the cloud server got expired at June 2021, the web application cannot be accessed online at this time.

## Overview
This is a online seat reservation system used for customer's purchasing and cinema's management. In this project, two applications were developed: one is an Android app used by customers, the other is a web app for administrators of the cinema to manage orders.
### Functionalities
  ![functionalities](https://user-images.githubusercontent.com/88390268/151494367-35a3190b-ef5e-4ac1-a187-7b6844347925.png)  

### Database Design
![database](https://user-images.githubusercontent.com/88390268/150656293-30464d56-df9a-49a9-ab43-fe59f72f05a5.png)

There're five tables in the database:
* Schedule :info of movie schedules, including movie id, hall id, date, start and end time, price, etc.
* Hall: info of different halls of the cinema
* User: user info, including unique id, password, tel, email, card number, vip identification, etc.
* OrderDetail: contains user id, schedule, hall and seat, method of payment, etc.
* Movie: contains the id, name, blurb, director, actors, picture and poster of a movie.
## Features 
### Cinema Conductor Side (Web app)
  * View weekly takings, overall and per-movie
  * Plot weekly takings graphically
  * Compare movies by number of tickets sold in a given time period
  * Show comparisons graphically
  * Provide a responsive, mobile-friendly user interface
### Customer Side (Android app)
  * Support user accounts and user login
        - Good securityn for user accounts, required a strong password
        - Required phone verification when changing passsword and first time log-in
        
  * View movie details (i.e.: blurb, certificate, director, lead actor/actress, etc.
    - Display promotional images associated with a movie
    - View screenings of a movie (date, time, cinema screen)
    - Search for a movie/screening by keyword or date
    - Buy ticket for a screening, the following options are provided:
        
        * adult
        * child (age <=16) available if appropriate to certificate
        * senior (age >=65) are available for a 20%. discount
  * See avaliable seats, and book a specific seat, for a screening
        - A visual representation of seating layout was provided
        - Support usage by multiple clients simultaneously(e.g.: selecting the same seat at one time)
  * Option to book a "VIP Seat" (prime position, more leg-room, costs extra)
  * Simulation of card payment and cash payment
        - Bind/unbind a card, and the payment page
        - Store user's card detailis for quicker checkout
        - Worker of the cinema can mark a ticket as paid on the Web app, if paid by cash
  * Send ticket via email, including QR code on ticket for validation
  * Store ticket, and display it on demand
  * Address issues of accessibility, provide different color & font choices
  * Here's a typical walkthrough:
 
    ![flowchart](https://user-images.githubusercontent.com/88390268/150656302-2fbe1575-3a2a-426c-976c-64938235b78c.png)
  
### Screenshots of User Interface 
 ![Workflow1](https://user-images.githubusercontent.com/88390268/151466702-880fe987-7fad-481b-bff9-faa108c404f8.png)
 
 ![Workflow2](https://user-images.githubusercontent.com/88390268/151493901-f901fc26-511b-4e4a-bc99-0d291fcce981.png)

