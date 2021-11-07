# Coco Cinema  

## Demo Video:  
- A demo video can be found at https://www.youtube.com/watch?v=PxzKoiAHjDQ 
- In the video, the Android application was installed at Huawei P30 Pro (screen size: 6.47", resolution: 1080*2340px)
- Note that because the cloud server got expired at June 2021, the web application cannot be accessed online at this time.

## Brief
- The project includes both Android and Web apps for cinema seat reservation, with functionalities raging from movie search & filtering, order & salesanalysys to data visualization.
- Programming language: Java, Python
- Framework and tool: Flask, Android Studio

## Features
- Customer Side (Android app)
    - Support user accounts and user login
        - Good securityn for user accounts, required a strong password
        - Required phone verification when changing passsword and first time log-in
    - View movie details (i.e.: blurb, certificate, director, lead actor/actress, etc.)
    - Display promotional images associated with a movie
    - View screenings of a movie (date, time, cinema screen)
    - Search for a movie/screening by keyword or date
    - Buy ticket for a screening, the following options are provided:
        - adult
        - child (age <=16) available if appropriate to certificate
        - senior (age >=65) are available for a 20%. discount
    - See avaliable seats, and book a specific seat, for a screening
        - A visual representation of seating layout was provided
        - Support usage by multiple clients simultaneously(e.g.: selecting the same seat at one time)
    - Option to book a "VIP Seat" (prime position, more leg-room, costs extra)
    - Simulation of card payment and cash payment
        - Bind/unbind a card, and the payment page
        - Store user's card detailis for quicker checkout
        - Worker of the cinema can mark a ticket as paid on the Web app, if paid by cash
    - Send ticket via email
        - QR code on ticket for validation
    - Store ticket, and display it on demand
    - Address issues of accessibility, provide different color & font choices
 - Cinema Conductor Side (Web app)
    - View weekly takings, overall and per-movie
    - Plot weekly takings graphically
    - Compare movies by number of tickets sold in a given time period
    - Show comparisons graphically
    - Provide a responsive, mobile-friendly user interface
 
