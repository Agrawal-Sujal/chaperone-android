<h1 align="center">Chaperone</h1>

<p align="center">Engage Locally, Donate Kindly.</p>


<img width="1765" height="990" alt="image" src="https://github.com/user-attachments/assets/34393f6d-55c7-44ed-97a8-d136ef908a5c" />

## 🚀 The Challenge: Isolation and Inactivity

For many individuals, especially seniors and those with mobility issues (like the RA/IBD community), isolation and inactivity are significant challenges. Finding safe, trustworthy companionship for a simple walk can be difficult, leading to reduced social interaction and physical movement.

## ✨ Our Solution

Chaperone directly addresses this by creating a vetted, peer-to-peer marketplace. We provide a secure and easy-to-use platform that connects two types of users:

- **Wanderers:** Those seeking companionship or mobility assistance for a walk.
    
- **Walkers:** Vetted individuals who provide companionship and assistance, earning money and supporting charity.
    

## Core Benefits

Our platform is built on four key pillars to ensure a safe and valuable experience for everyone.

- **🤝 Trusted Companionship:** All Walkers are ID-verified for your security.
    
- **♿ Mobility Assistance:** Easily find a companion who can match your pace and provide assistance.
    
- **💬 Reduced Social Isolation:** Transform a simple walk into a meaningful social connection.
    
- **🏃 Encouraged Physical Movement:** Stay active and motivated with a reliable partner.
    

## 📱 How It Works

### 1. One App, Two Unique Experiences

From the start, users select their role. This tailors the entire app experience, ensuring Wanderers only see booking and safety features, while Walkers see earning and request-management tools.


### 2. Safety & Trust First: ID Verification

Trust is non-negotiable. All users, especially Walkers, must complete an ID verification process (using Aadhaar) before they can take their first walk, ensuring a secure network.

### 3. The Wanderer Flow: Booking a Walk

The Wanderer's experience is designed to be simple and secure, getting them from booking to a safe walk in just a few taps.

1. **Schedule:** The Wanderer sets a time, location, and their preferences (pace, mobility needs, etc.).
    
2. **Select:** They are shown a list of available, verified Walkers who match their criteria.
    
3. **Request:** After viewing a Walker's profile, they send a request, which is securely paid for using our Razorpay integration.

### 4. The Walker Flow: Accepting a Request

The Walker's experience is built for flexibility and earning.

1. **Set Availability:** Walkers set their availability radius (e.g., "willing to walk up to 5km") and go "Online."
    
2. **Explore Requests:** They see a feed of incoming walk requests from Wanderers nearby.
    
3. **Accept & Earn:** The Walker reviews the Wanderer's needs and accepts the request.

### 5. Live Tracking, Safety & Feedback

This is the core of our safety system.

- **Automated Activation:** Once a walk's scheduled time begins, "Track Location" buttons are automatically activated for both users.
    
- **Live GPS Monitoring:** Both users can see each other's live location on a map until the walk is marked complete. An SOS button is available at all times.
    
- **Two-Way Ratings:** After the walk, both the Wanderer and Walker are required to rate each other. This mandatory feedback loop maintains network quality and safety.
    

### 6. Transparent Earnings & Donations

A portion of every Wanderer's payment goes directly to a charity of their choice, which they can track in their "My Charities" section. Walkers can track their complete earnings and transaction history in their "My Wallet" section.


## ⚖️ Balancing the Ecosystem

A key challenge in a peer-to-peer marketplace is balancing supply and demand. To prevent a surplus of Walkers and ensure only dedicated companions remain active, we implemented a Walker subscription model after a successful free trial. This nominal fee creates a higher-quality, more reliable network for Wanderers.

## 🛠️ Technology Stack

Chaperone is built with a modern, high-performance tech stack.

|   |   |
|---|---|
|**Category**|**Technology**|
|**Frontend (Client-Side)**|Kotlin & Jetpack Compose|
|**Backend (Server-Side)**|Django (Python)|
|**Real-Time Communication**|Django Channels (WebSockets)|
|**Database**|PostgreSQL|
|**Payment Gateway**|Razorpay SDK|
|**APIs & Services**|Google Maps SDK, Firebase Cloud Messaging (FCM)|

## ⚙️ Key Technical Features

1. **Real-Time GPS Tracking via WebSockets:** We used Django Channels to create a persistent, bidirectional WebSocket connection. This allows for efficient, low-latency broadcasting of GPS coordinates between both users during an active walk for a smooth, live tracking experience.
    
2. **Secure Payment Flow:** We integrated the Razorpay SDK to handle payments securely. The client-side handles the transaction, and our Django backend verifies the final transaction status to confirm the walk.
    
3. **Instant Walk Request Notifications:** We used Firebase Cloud Messaging (FCM) to send silent, real-time push notifications to Walkers when a Wanderer requests a walk, allowing for immediate responses.
    

## 👥 Our Team (Triverse Ignisia)

- **Bhoomi Agarwal** - Designer
    
- **Harsh Bhadauria** - Frontend (Kotlin)
    
- **Sujal Agrawal** - Backend (Django)
