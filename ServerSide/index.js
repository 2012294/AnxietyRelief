const express = require("express");
const cors = require("cors");
const multer = require('multer');
const path = require('path');
const nodemailer = require('nodemailer');


const { v4: uuidv4 } = require('uuid'); 
const { MongoClient, ObjectId } = require('mongodb');


const url = 'mongodb://localhost:27017/';
const dbName = 'AnxietyRelief_db';
const client = new MongoClient(url);

client.connect().then(() => {
    console.log('Connected to MongoDB');
}).catch(err => {
    console.error('Error connecting to MongoDB:', err);
});

const db = client.db(dbName);

const app = express();



app.use(express.json({ limit: "50mb" })); 
app.use(express.urlencoded({ limit: "50mb", extended: true }));
app.use(cors());

const Patients = db.collection("Patients"); 
const Therapists = db.collection("Therapists");

const Affirmation =db.collection("affirmations");
const Themes = db.collection("themesAffirmation");
const AnxietyLevel = db.collection("AnxietyLevel");
const JournalingPrompts = db.collection("JournalingPrompts")
const JournalEntries = db.collection("JournalEntries");
const BreathingExercises = db.collection("BreathingExercises");
const Transactions = db.collection("Transactions");
const Appointments = db.collection("Appointments");
const chatRoomsCollection = db.collection("ChatMessages");

const storage = multer.diskStorage({
    destination: function(req, file, cb) {
        cb(null, 'uploads/'); 
    },
    filename: function(req, file, cb) {
        cb(null, file.originalname); 
    }
});

const upload = multer({ storage: storage });

const fs = require('fs');

const bcrypt = require('bcrypt');


const Cards = db.collection("Cards");


const CryptoJS = require("crypto-js");
const crypto = require('crypto');

const seed = 'Anx13tyR3lief$3ed';


function generateKey(seed) {
    return crypto.createHash('sha256').update(seed).digest('hex');
}


const key = generateKey(seed);

console.log('Generated Key:', key);

const WebSocket = require('ws');


const wss = new WebSocket.Server({ port: 8080 });



const rooms = new Map();
const previousMessagesSent = new Map();

wss.on('connection', (ws) => {
    console.log('Client connected');

    ws.on('message', async (message) => {
        console.log(`Received: ${message}`);
        const data = JSON.parse(message);
        const { event, roomId, text, sender, PatientName, SessionDate, SessionTime, role, TherapistID } = data;

        if (event === 'message') {
            console.log('Received message:', { roomId, text, sender });

            if (sender !== 'therapist' && sender !== 'patient') {
                console.log('Invalid sender:', sender);
                return;
            }

            let room = rooms.get(roomId);
            if (!room) {
                room = { therapist: null, patient: null };
                rooms.set(roomId, room);
            }

            await chatRoomsCollection.updateOne(
                { roomId },
                {
                    $set: {
                        PatientName,
                        SessionDate,
                        SessionTime,
                        TherapistID
                    },
                    $push: {
                        messages: {
                            text,
                            sender,
                            timestamp: new Date()
                        }
                    }
                },
                { upsert: true }
            );

            const recipientSocket = sender === 'therapist' ? room.patient : room.therapist;
            if (recipientSocket && recipientSocket.readyState === WebSocket.OPEN) {
                recipientSocket.send(JSON.stringify({ event: 'msg', text, sender }));
                console.log(`Message sent from ${sender} to ${recipientSocket === room.patient ? 'patient' : 'therapist'}`);
            } else {
                console.log('Recipient not connected, message saved to be sent later');
            }
        } else if (event === 'register') {
            console.log('Received registration:', { roomId, role });

            let room = rooms.get(roomId);
            if (!room) {
                room = { therapist: null, patient: null };
                rooms.set(roomId, room);
                console.log('Room created:', roomId);
            }

            if (role === 'therapist') {
                room.therapist = ws;
                console.log('Therapist registered in room:', roomId);
                notifyOtherParty(room, ws, 'therapist', 'therapist_online', 'therapist_reconnected', roomId);
            } else if (role === 'patient') {
                room.patient = ws;
                console.log('Patient registered in room:', roomId);
                notifyOtherParty(room, ws, 'patient', 'patient_online', 'patient_reconnected', roomId);
            }

            if (room.therapist && room.therapist.readyState === WebSocket.OPEN && room.patient && room.patient.readyState === WebSocket.OPEN) {
                room.therapist.send(JSON.stringify({ event: 'patient_online' }));
                room.patient.send(JSON.stringify({ event: 'therapist_online' }));
            }

            console.log('Room:', room);
        }
    });

    ws.on('close', () => {
        console.log('Connection closed');

        rooms.forEach((room, roomId) => {
            if (room.therapist === ws) {
                room.therapist = null;
                console.log('Therapist disconnected from room:', roomId);
                if (room.patient && room.patient.readyState === WebSocket.OPEN) {
                    room.patient.send(JSON.stringify({ event: 'therapist_offline' }));
                }
            } else if (room.patient === ws) {
                room.patient = null;
                console.log('Patient disconnected from room:', roomId);
                if (room.therapist && room.therapist.readyState === WebSocket.OPEN) {
                    room.therapist.send(JSON.stringify({ event: 'patient_offline' }));
                }
            }
        });
    });
});

async function sendPreviousMessages(ws, roomId) {
    try {
        const room = await chatRoomsCollection.findOne({ roomId });
        if (room && room.messages) {
            room.messages.forEach(msg => ws.send(JSON.stringify({ event: 'msg', ...msg })));
        }
    } catch (error) {
        console.error('Error fetching previous messages:', error);
    }
}

function notifyOtherParty(room, ws, role, onlineEvent, reconnectedEvent, roomId) {
    const otherParty = role === 'therapist' ? room.patient : room.therapist;
    if (otherParty && otherParty.readyState === WebSocket.OPEN) {
        otherParty.send(JSON.stringify({ event: onlineEvent }));
    }

    if (otherParty) {
        ws.send(JSON.stringify({ event: reconnectedEvent }));

       
        if (!previousMessagesSent.has(ws)) {
            sendPreviousMessages(ws, roomId);
            previousMessagesSent.set(ws, true);
        }
    } else {
     
        if (!previousMessagesSent.has(ws)) {
            sendPreviousMessages(ws, roomId);
            previousMessagesSent.set(ws, true);
        }
    }
}
app.post('/saveCard/:patientId', async (req, res) => {
    try {
        const patientId = req.params.patientId;
        const cardList = req.body; 
        await Cards.deleteMany({ patientId: patientId });

        
        const encryptedCardList = await Promise.all(cardList.map(async (cardData) => {
            const { cardNumber, cardname, cvv, month, year } = cardData;
            const encryptedCardData = CryptoJS.AES.encrypt(JSON.stringify({ cardNumber, cardname, cvv, month, year }), key).toString();
            return encryptedCardData;
        }));

    
        const result = await Cards.insertMany(encryptedCardList.map(encryptedCardData => ({
            patientId: patientId,
            encryptedCardData: encryptedCardData
        })));

      
        res.status(200).json({ message: 'Cards saved successfully', cardIds: result.insertedIds });
    } catch (error) {
        console.error('Error saving cards:', error);
        res.status(500).json({ error: 'Internal server error' });
    }
});





app.post("/saveTransactions", async (req, res) => {
    try {
        const { therapistId, patientId, therapistAccountNumber, patientAccountNumber, charges, status } = req.body;

       
        if (!therapistId || !patientId || !therapistAccountNumber || !patientAccountNumber || !charges || !status) {
            return res.status(400).json({ message: "Missing required fields" });
        }

       
        const currentTime = new Date();

        const date = new Date(currentTime);
            const localDate = date.toLocaleString();

       
        const transaction = {
            therapistId,
            patientId,
            therapistAccountNumber,
            patientAccountNumber,
            charges,
            status,
            time: localDate 
        };

        
        const result = await Transactions.insertOne(transaction);

      
        const insertedId = result.insertedId;
        res.setHeader('Transaction-Id', insertedId.toString());
        res.status(201).json({ message: "Transaction saved successfully", transactionId: insertedId.toString() });
    } catch (error) {
        console.error("Error saving transaction:", error);
        res.status(500).json({ message: "Internal server error" });
    }
});

app.post("/saveAppointment", async (req, res) => {
    try {
        const { therapistId, patientId, SessionType, day, time,transactionId,therapistname,patientname} = req.body;

      
        const appointment = {
            therapistId,
            patientId,
            SessionType,
            day,
            time,
            transactionId,
            therapistname,
            patientname
    
        };

      
        const result = await Appointments.insertOne(appointment);

       
        const insertedId = result.insertedId;
        res.setHeader('Transaction-Id', insertedId.toString());
        res.status(201).json({ message: "Transaction saved successfully", transactionId: insertedId.toString() });
    } catch (error) {
        console.error("Error saving transaction:", error);
        res.status(500).json({ message: "Internal server error" });
    }
});


app.get("/gettherapistAppointments/:therapistId", async (req, res) => {
    try {
        const therapistId = req.params.therapistId;

  
        const appointments = await Appointments.find({ therapistId: therapistId }).toArray();

       
        res.status(200).json(appointments);
    } catch (error) {
        console.error("Error fetching appointments:", error);
        res.status(500).json({ message: "Internal server error" });
    }
});


  
  


app.get('/getCards/:patientId', async (req, res) => {
    try {
        const patientId = req.params.patientId;

       
        const cursor = await Cards.find({ patientId });

       
        const cards = await cursor.toArray();

        if (!cards || cards.length === 0) {
            return res.status(404).json({ message: 'No cards found for the given patientId' });
        }

      
        const decryptedCardList = cards.map(card => {
            try {
                const decryptedBytes = CryptoJS.AES.decrypt(card.encryptedCardData, key);
                const decryptedData = decryptedBytes.toString(CryptoJS.enc.Utf8);
                return JSON.parse(decryptedData);
            } catch (error) {
                console.error('Error decrypting card data:', error);
                return null; 
            }
        });

       
        const validDecryptedCards = decryptedCardList.filter(card => card !== null);

        
        const extractedFields = validDecryptedCards.map(card => ({
            cardNumber: card.cardNumber,
            cardname: card.cardname,
            cvv: card.cvv,
            month: card.month,
            year: card.year
        }));

      
        
        res.status(200).json(extractedFields);
    } catch (error) {
        console.error('Error retrieving cards:', error);
        res.status(500).json({ error: 'Internal server error' });
    }
});
app.post('/saveAccount/:therapistId', async (req, res) => {
    try {
        const therapistId = req.params.therapistId;
        console.log('Therapist ID:', therapistId);
        const accountList = req.body; 
        
      
        const encryptedAccountList = await Promise.all(accountList.map(async (accountData) => {
            const { Number, name } = accountData;
            const encryptedAccountData = CryptoJS.AES.encrypt(JSON.stringify({ Number, name }), key).toString();
            return encryptedAccountData;
        }));

      
        const therapist = await Therapists.findOne({ _id: new ObjectId(therapistId) });

        if (!therapist) {
           
            return res.status(404).json({ error: 'Therapist not found' });
        }

    
        const result = await Therapists.updateOne(
            { _id: new ObjectId(therapistId) },
            { $set: { accountList: encryptedAccountList } },
           
        );
        

        // Send a success response
        res.status(200).json({ message: 'Accounts saved successfully', accountList: encryptedAccountList });
    } catch (error) {
        console.error('Error saving accounts:', error);
        res.status(500).json({ error: 'Internal server error' });
    }
});









app.delete('/deletePdfFiles/:therapistId', async (req, res) => {
    try {
        const therapistId = req.params.therapistId;
        
       
        const therapist = await Therapists.findOne({ _id: new ObjectId(therapistId) });

        if (!therapist) {
            return res.status(404).json({ error: 'Therapist not found' });
        }
        
        
        const pdfFiles = therapist.pdfFiles;

        // Check if pdfFiles exist
        if (!pdfFiles || pdfFiles.length === 0) {
            return res.status(404).json({ error: 'No PDF files found for this therapist' });
        }

        
        pdfFiles.forEach(async (file) => {
            const filePath = file.pdfPath;

          
            fs.unlinkSync(filePath);
        });

       
        await Therapists.updateOne(
            { _id: new ObjectId(therapistId) },
            { $set: { pdfFiles: [] } }
        );

    
        res.status(200).json({ message: 'PDF files deleted successfully' });
    } catch (error) {
        console.error('Error deleting PDF files:', error);
        res.status(500).json({ error: 'Internal server error' });
    }
});

app.post('/upload/:therapistId', upload.array('pdf'), async (req, res) => {
    try {
        const therapistId = req.params.therapistId;
        const files = req.files; 

       
        const existingTherapist = await Therapists.findOne({ _id: new ObjectId(therapistId) });
        const existingPdfFiles = existingTherapist ? existingTherapist.pdfFiles : [];

        if (files && files.length > 0) {
          
            const newPdfFiles = files.map(file => ({
                originalPdfName: file.originalname,
                pdfPath: file.path
            }));

           
            if (existingPdfFiles && existingPdfFiles.length > 0 && JSON.stringify(existingPdfFiles) === JSON.stringify(newPdfFiles)) {
                console.log('Same data already exists. No action taken.');
                return res.status(200).send('Same data already exists. No action taken.');
            }

           
            if (existingPdfFiles && existingPdfFiles.length > 0) {
                existingPdfFiles.forEach(file => {
                    if (!newPdfFiles.some(newFile => newFile.originalPdfName === file.originalPdfName)) {
                        fs.unlinkSync(file.pdfPath); 
                    }
                });
            }

      
            await Therapists.updateOne(
                { _id: new ObjectId(therapistId) },
                { $set: { pdfFiles: [] } }
            );

           
            await Therapists.updateOne(
                { _id: new ObjectId(therapistId) },
                { $push: { pdfFiles: { $each: newPdfFiles } } }
            );

            return res.status(200).send('Files uploaded successfully.');
        } else {
          
            if (existingPdfFiles && existingPdfFiles.length > 0) {
                existingPdfFiles.forEach(file => {
                    fs.unlinkSync(file.pdfPath); 
                });
            }

            await Therapists.updateOne(
                { _id: new ObjectId(therapistId) },
                { $set: { pdfFiles: [] } }
            );

            return res.status(200).send('All files deleted successfully.');
        }
    } catch (err) {
        console.error('Error uploading files:', err);
        res.status(500).send('Error uploading files.');
    }
});





app.get("/getPatient/:userId", async (req, res) => {
    const userId = req.params.userId;

    try {
        const patient = await Patients.findOne({ _id: new ObjectId(userId) });

        if (patient) {
            res.json(patient);
        } else {
            res.status(404).json({ error: "Patient not found" });
        }
    } catch (error) {
        console.error("Error getting user:", error);
        res.status(500).json({ error: 'Failed to get user. ${error.message}' });
    }
});
app.get("/getTherapist/:userId", async (req, res) => {
    const userId = req.params.userId;

    try {
        const therapist = await Therapists.findOne({ _id: new ObjectId(userId) });

        if (!therapist) {
            return res.status(404).json({ error: "Therapist not found" });
        }
        const pdfFiles = therapist.pdfFiles ? await Promise.all(therapist.pdfFiles.map(async pdf => {
           
            const pdfData = await fs.promises.readFile(pdf.pdfPath);
          
            const base64PdfData = pdfData.toString('base64');
            return {
                originalPdfName: pdf.originalPdfName,
                pdfData: base64PdfData 
            };
        })) : null;
       
        therapist.pdfFiles = pdfFiles;
        if (therapist) {
            res.json(therapist);
        } else {
            res.status(404).json({ error: "Therapist not found" });
        }
        
    } catch (error) {
        console.error("Error getting therapist:", error);
        res.status(500).json({ error: "Failed to get therapist: ${error.message}" });
    }
});

app.post("/registerTherapist", async (req, res) => {
    const { username, email, password } = req.body;

    
    const existingTherapist = await Therapists.findOne({ email });

    if (existingTherapist) {
        return res.status(400).json({ error: "Email already registered" });
    }

    const newTherapist = {
        username,
        email,
        password,
    };

    try {
        const result = await Therapists.insertOne(newTherapist);

        if (result.acknowledged && result.insertedId) {
            res.json({ _id: result.insertedId, ...newTherapist }); 
        } else {
            res.status(500).json({ error: "Failed to insert user. No user inserted." });
        }
    } catch (error) {
        console.error("Error inserting user:", error);
        res.status(500).json({ error: 'Failed to insert user. ${error.message}' });
    }
});

app.post("/registerPatient", async (req, res) => {
    const { username, email, password } = req.body;

    
    const existingPatient = await Patients.findOne({ email });

    if (existingPatient) {
        return res.status(400).json({ error: "Email already registered" });
    }

    const newPatient = {
        username,
        email,
        password,
    };

    try {
        const result = await Patients.insertOne(newPatient);

        if (result.acknowledged && result.insertedId) {
            res.json({ _id: result.insertedId, ...newPatient }); // Return the inserted user
        } else {
            res.status(500).json({ error: "Failed to insert user. No user inserted." });
        }
    } catch (error) {
        console.error("Error inserting user:", error);
        res.status(500).json({ error: 'Failed to insert user. ${error.message}' });
    }
});

app.post("/loginPatients", async (req, res) => {
    const { email, password } = req.body;

    try {
        const patient = await Patients.findOne({ email });

        if (!patient) {
            return res.status(401).json({ error: "Email not registered" });
        }

        if (patient.password !== password) {
            return res.status(401).json({ error: "Incorrect password" });
        }

        res.json({ _id: patient._id, username: patient.username, email: patient.email });
    } catch (error) {
        console.error("Error during login:", error);
        res.status(500).json({ error: 'Failed to login. ${error.message}' });
    }
});
app.post("/loginTherapists", async (req, res) => {
    const { email, password } = req.body;

    try {
        const therapist = await Therapists.findOne({ email });

        if (!therapist) {
            return res.status(401).json({ error: "Email not registered" });
        }

        if (therapist.password !== password) {
            return res.status(401).json({ error: "Incorrect password" });
        }

        res.json({ _id: therapist._id, username: therapist.username, email: therapist.email });
    } catch (error) {
        console.error("Error during login:", error);
        res.status(500).json({ error:' Failed to login. ${error.message}' });
    }
});

app.get("/checkUsernamePatientAvailability/:username", async (req, res) => {
    const username = req.params.username;

    try {
        const existingPatient = await Patients.findOne({ username });

        if (existingPatient) {
           
            return res.json({ available: false });
        } else {
           
            return res.json({ available: true });
        }
    } catch (error) {
        console.error("Error checking username availability:", error);
        res.status(500).json({ error: 'Failed to check username availability. ${error.message}' });
    }
});
app.get("/checkUsernameTherapistAvailability/:username", async (req, res) => {
    const username = req.params.username;
  

    try {
        const existingTherapist = await Therapists.findOne({ username });

        if (existingTherapist) {
            // Username already exists
            return res.json({ available: false });
        } else {
            // Username is available
            return res.json({ available: true });
        }
    } catch (error) {
        console.error("Error checking username availability:", error);
        res.status(500).json({ error: 'Failed to check username availability. ${error.message}' });
    }
});
app.post("/saveFavoriteStatus", async (req, res) => {
    const { patientId, id, isFavorite, quote } = req.body;
    console.log("Received request to /saveFavoriteStatus");
    console.log("Request Body:", req.body);

    try {
        const filter = { patientId };
        const update = {
            $addToSet: {  
                favorites: {
                    id,
                    isFavorite,
                    quote,
                },
            },
        };

        const result = await Affirmation.updateOne(filter, update, { upsert: true });

        console.log("Update Result:", result);

        if (result.modifiedCount > 0 || result.upsertedCount > 0) {
            res.json({ success: true });
        } else {
            res.status(404).json({ error: "No update or upsert occurred" });
        }
    } catch (error) {
        console.error("Error saving favorite status:", error);
        res.status(500).json({ error: "Failed to save favorite status" });
    }
});

app.post("/deleteFavoriteStatus", async (req, res) => {
    const { patientId, id } = req.body;

    try {
        const filter = { patientId };
        const update = {
            $pull: {
                favorites: {
                    id: id,
                },
            },
        };

        const result = await Affirmation.updateOne(filter, update);

        console.log("Delete Result:", result);

        if (result.modifiedCount > 0) {
            res.json({ success: true });
        } else {
            res.status(404).json({ error: "No matching affirmation found" });
        }
    } catch (error) {
        console.error("Error deleting favorite status:", error);
        res.status(500).json({ error: "Failed to delete favorite status" });
    }
});




app.get("/getAffirmations/:patientId", async (req, res) => {
    const patientId = req.params.patientId;

    try {
        const affirmations = await Affirmation.find({ patientId });

        res.json(affirmations);
    } catch (error) {
        console.error("Error getting affirmations:", error);
        res.status(500).json({ error: "Failed to get affirmations" });
    }
});

app.get('/getFavoriteStatus', async (req, res) => {
    const { patientId, affirmationId } = req.query;

    try {
        const affirmation = await Affirmation.findOne(
            { patientId },
            { favorites: 1 } 
        );

        if (affirmation && affirmation.favorites.length > 0) {
            const matchedFavorite = affirmation.favorites.find(fav => fav.id === affirmationId);

            if (matchedFavorite) {
                res.json({ isFavorite: matchedFavorite.isFavorite });
            } else {
                res.status(404).json({ error: 'Affirmation not found in favorites' });
            }
        } else {
            res.status(404).json({ error: 'Patient or favorite not found' });
        }
    } catch (error) {
        console.error('Error getting favorite status:', error);
        res.status(500).json({ error: 'Failed to get favorite status' });
    }
});



app.get('/getFavoriteAffirmations', async (req, res) => {
    const { patientId } = req.query;

    try {
        const favoriteAffirmations = await Affirmation.find({
            patientId,
            'favorites.isFavorite': true
        }).toArray();

        console.log('Favorite affirmations:', favoriteAffirmations);

        res.json(favoriteAffirmations);
    } catch (error) {
        console.error('Error getting favorite affirmations:', error);
        res.status(500).json({ error: 'Failed to get favorite affirmations' });
    }
});

app.post("/saveAffirmation", async (req, res) => {
    const { patientId, affirmationText } = req.body;

    try {
        const affirmationKey = uuidv4();

        const existingPatient = await Affirmation.findOne({ patientId });

        if (existingPatient) {
            
            if (!existingPatient.own) {
                existingPatient.own = [];
            }
            existingPatient.own.push({ id: affirmationKey, quote: affirmationText });

          
            const result = await Affirmation.updateOne(
                { patientId },
                {
                    $set: {
                        own: existingPatient.own,
                    },
                }
            );

            if (result.modifiedCount > 0) {
                res.json({ success: true, affirmationKey });
            } else {
                res.status(500).json({ error: "Failed to save affirmation" });
            }
        } else {
            const newAffirmation = {
                patientId,
                favorites: [],
                own: [{ id: affirmationKey, quote: affirmationText }],
            };

            const result = await Affirmation.insertOne(newAffirmation);

            if (result.acknowledged && result.insertedId) {
                res.json({ success: true, affirmationKey });
            } else {
                res.status(500).json({ error: "Failed to save affirmation" });
            }
        }
    } catch (error) {
        console.error("Error saving affirmation:", error);
        res.status(500).json({ error: "Failed to save affirmation" });
    }
});


app.post("/deleteAffirmation", async (req, res) => {
    const { patientId, affirmationId } = req.body;
    try {
        const filter = { patientId };
        const update = {
            $pull: {
                own: {
                    id: affirmationId,
                },
            },
        };

        const result = await Affirmation.updateOne(filter, update);

        console.log("Delete Result:", result);

        if (result.modifiedCount > 0) {
            res.json({ success: true });
        } else {
            res.status(404).json({ error: "No matching affirmation found" });
        }
    } catch (error) {
        console.error("Error deleting affirmation:", error);
        res.status(500).json({ error: "Failed to delete affirmation" });
    }
});

app.get("/getOwnAffirmations/:patientId", async (req, res) => {
    const patientId = req.params.patientId;

    try {
        const affirmations = await Affirmation.findOne({ patientId });

        if (affirmations && affirmations.own) {
            res.json(affirmations.own);
        } else {
            res.status(404).json({ error: 'No own affirmations found' });
        }
    } catch (error) {
        console.error("Error getting own affirmations:", error);
        res.status(500).json({ error: 'Failed to get own affirmations' });
    }
});
app.post("/updateAffirmation", async (req, res) => {
    const { patientId, affirmationId, newQuote } = req.body;

    try {
        const filter = { patientId, "own.id": affirmationId };
        const update = {
            $set: {
                "own.$.quote": newQuote,
            },
        };

        const result = await Affirmation.updateOne(filter, update);

        if (result.modifiedCount > 0) {
            res.json({ success: true });
        } else {
            res.status(404).json({ error: "No matching affirmation found" });
        }
    } catch (error) {
        console.error("Error updating affirmation:", error);
        res.status(500).json({ error: "Failed to update affirmation" });
    }
});
app.post("/saveTheme", async (req, res) => {
    const { patientId, wallpaperImage, textColor } = req.body;

    try {
   
        const existingTheme = await Themes.findOne({ patientId });

        if (existingTheme) {
     
            const result = await Themes.updateOne(
                { patientId },
                {
                    $set: {
                        wallpaperImage,
                        textColor,
                    },
                }
            );

            if (result.modifiedCount > 0) {
                res.json({ success: true });
            } else {
                res.status(500).json({ error: "Failed to update theme" });
            }
        } else {
          
            const result = await Themes.insertOne({
                patientId,
                wallpaperImage,
                textColor,
            });

            if (result.acknowledged && result.insertedId) {
                res.json({ success: true });
            } else {
                res.status(500).json({ error: "Failed to save theme" });
            }
        }
    } catch (error) {
        console.error("Error saving theme:", error);
        res.status(500).json({ error: "Failed to save theme" });
    }
});


app.get("/getTheme/:patientId", async (req, res) => {
    const patientId = req.params.patientId;

    try {
      
        const theme = await Themes.findOne({ patientId });

        if (theme) {
            res.json(theme);
        } else {
            res.status(404).json({ error: "Theme not found for the patient" });
        }
    } catch (error) {
        console.error("Error getting theme:", error);
        res.status(500).json({ error: "Failed to get theme" });
    }
});

app.post("/saveAnxietyLevel", async (req, res) => {
    const { patientId, anxietyLevel, DateTime } = req.body;
    console.log("Received request to /saveAnxietyLevel");
    console.log("Request Body:", req.body);

    try {
        const result = await AnxietyLevel.insertOne({
            patientId,
            anxietyLevel,
            DateTime
        });

        if (result.acknowledged && result.insertedId) {
            res.json({ success: true });
        } else {
            res.status(500).json({ error: "Failed to save anxiety level" });
        }
    } catch (error) {
        console.error("Error saving anxiety level:", error);
        res.status(500).json({ error: "Failed to save anxiety level" });
    }
});

app.get("/getAnxietyLevels/:patientId", async (req, res) => {
    const patientId = req.params.patientId;

    try {
        const anxietyLevels = await AnxietyLevel.find({ patientId }).toArray();

        res.json(anxietyLevels);
    } catch (error) {
        console.error("Error getting anxiety levels:", error);
        res.status(500).json({ error: "Failed to get anxiety levels" });
    }
});

app.post("/deleteAnxietyLevel", async (req, res) => {
    const { patientId, _id } = req.body;

    try {
        const result = await AnxietyLevel.deleteOne({ patientId, _id: new ObjectId(_id) });

        if (result.deletedCount > 0) {
            res.json({ success: true });
        } else {
            res.status(404).json({ error: "No matching anxiety level found for deletion" });
        }
    } catch (error) {
        console.error("Error deleting anxiety level:", error);
        res.status(500).json({ error: "Failed to delete anxiety level" });
    }
});

app.get("/getAvailablePrompts/:patientId", async (req, res) => {
    const patientId = req.params.patientId;

    try {
      
        const allPrompts = await JournalingPrompts.find({}).toArray();

       
        const usedPrompts = await JournalEntries.distinct("prompt", { patient_id: patientId });

       
        const availablePrompts = allPrompts.filter(prompt => !usedPrompts.includes(prompt.prompt));

        res.json(availablePrompts);
    } catch (error) {
        console.error("Error getting available prompts:", error);
        res.status(500).json({ error: "Failed to get available prompts" });
    }
});


app.get("/getJournalEntries/:patientId", async (req, res) => {
    const patientId = req.params.patientId;

    try {
        const entries = await JournalEntries.find({ patient_id: patientId }).toArray();
        res.json(entries);
    } catch (error) {
        console.error("Error getting journal entries:", error);
        res.status(500).json({ error: "Failed to get journal entries" });
    }
});




app.put("/updateJournalEntry/:entryId", async (req, res) => {
    const entryId = req.params.entryId;
    const updatedEntry = req.body;

    try {
        const result = await JournalEntries.updateOne({ _id: new ObjectId(entryId) }, { $set: updatedEntry });

        if (result.modifiedCount > 0) {
            res.json({ success: true });
        } else {
            res.status(404).json({ error: "Journal entry not found for the given ID" });
        }
    } catch (error) {
        console.error("Error updating journal entry:", error);
        res.status(500).json({ error: "Failed to update journal entry" });
    }
});


app.delete("/deleteJournalEntry/:entryId", async (req, res) => {
    const entryId = req.params.entryId;

    try {
        const result = await JournalEntries.deleteOne({ _id: new ObjectId(entryId) });

        if (result.deletedCount > 0) {
            res.json({ success: true });
        } else {
            res.status(404).json({ error: "Journal entry not found for the given ID" });
        }
    } catch (error) {
        console.error("Error deleting journal entry:", error);
        res.status(500).json({ error: "Failed to delete journal entry" });
    }
});

app.post("/saveJournalEntry", async (req, res) => {
    const { patient_id, prompt, day, date, time, entryText } = req.body;

    const newJournalEntry = {
        patient_id,
        prompt,
        day,
        date,
        time,
        entryText,
    };

    try {
        const result = await JournalEntries.insertOne(newJournalEntry);

        if (result.acknowledged && result.insertedId) {
            res.json({ _id: result.insertedId, ...newJournalEntry });
        } else {
            res.status(500).json({ error: "Failed to insert journal entry. No entry inserted." });
        }
    } catch (error) {
        console.error("Error inserting journal entry:", error);
        res.status(500).json({ error:" Failed to insert journal entry. ${error.message} "});
    }
});

app.post("/insertBreathingExercises", async (req, res) => {
    const { patient_id, EqualBreathing, BoxBreathing, fourseveneightBreathing, trianglebreathing, music } = req.body;

    const newExercise = {
       patient_id,
       EqualBreathing,
       BoxBreathing,
       fourseveneightBreathing,
       trianglebreathing,
       music,
    };

    try {
        const result = await BreathingExercises.insertOne(newExercise);

        if (result.acknowledged && result.insertedId) {
            res.json({ _id: result.insertedId, ...newExercise });
        } else {
            res.status(500).json({ error: "Failed to insert breathing exercise. No exercise inserted." });
        }
    } catch (error) {
        console.error("Error inserting breathing exercise:", error);
        res.status(500).json({ error: "Failed to insert breathing exercise. ${error.message}" });
    }
});

app.get("/getBreathingExercises/:patientId", async (req, res) => {
    const patientId = req.params.patientId;

    try {
        const entries = await BreathingExercises.find({ patient_id: patientId }).toArray();
        res.json(entries);
    } catch (error) {
        console.error("Error getting journal entries:", error);
        res.status(500).json({ error: "Failed to get journal entries" });
    }
});

app.put("/updateEqualBreathing/:patientId", async (req, res) => {
    const patientId = req.params.patientId;
    const { equalBreathingValue } = req.body;

    try {
        const result = await BreathingExercises.updateOne(
            { patient_id: patientId },
            { $set: { EqualBreathing: equalBreathingValue } },
            
        );

        if (result.acknowledged && result.modifiedCount > 0) {
            res.json({ success: true, message: "EqualBreathing value updated successfully." });
        } else {
            res.status(404).json({ error: "Patient not found or EqualBreathing value not updated." });
        }
    } catch (error) {
        console.error("Error updating EqualBreathing value:", error);
        res.status(500).json({ error: "Failed to update EqualBreathing value. ${error.message} "});
    }
});

app.put("/updateBoxBreathing/:patientId", async (req, res) => {
    const patientId = req.params.patientId;
    const { boxBreathingValue } = req.body;

    try {
        const result = await BreathingExercises.updateOne(
            { patient_id: patientId },
            { $set: { BoxBreathing: boxBreathingValue } },
            
        );

        if (result.acknowledged && result.modifiedCount > 0) {
            res.json({ success: true, message: "EqualBreathing value updated successfully." });
        } else {
            res.status(404).json({ error: "Patient not found or EqualBreathing value not updated." });
        }
    } catch (error) {
        console.error("Error updating EqualBreathing value:", error);
        res.status(500).json({ error: "Failed to update EqualBreathing value. ${error.message} "});
    }
});

app.put("/update478Breathing/:patientId", async (req, res) => {
    const patientId = req.params.patientId;
    const { FourseveneightBreathingValue } = req.body;

    try {
        const result = await BreathingExercises.updateOne(
            { patient_id: patientId },
            { $set: { fourseveneightBreathing: FourseveneightBreathingValue} },
            
        );

        if (result.acknowledged && result.modifiedCount > 0) {
            res.json({ success: true, message: "EqualBreathing value updated successfully." });
        } else {
            res.status(404).json({ error: "Patient not found or EqualBreathing value not updated." });
        }
    } catch (error) {
        console.error("Error updating EqualBreathing value:", error);
        res.status(500).json({ error: "Failed to update EqualBreathing value. ${error.message} "});
    }
});

app.put("/updateTriangleBreathing/:patientId", async (req, res) => {
    const patientId = req.params.patientId;
    const { TriangleBreathingValue } = req.body;

    try {
        const result = await BreathingExercises.updateOne(
            { patient_id: patientId },
            { $set: { trianglebreathing: TriangleBreathingValue } },
            
        );

        if (result.acknowledged && result.modifiedCount > 0) {
            res.json({ success: true, message: "EqualBreathing value updated successfully." });
        } else {
            res.status(404).json({ error: "Patient not found or EqualBreathing value not updated." });
        }
    } catch (error) {
        console.error("Error updating EqualBreathing value:", error);
        res.status(500).json({ error: "Failed to update EqualBreathing value. ${error.message} "});
    }
});

app.put("/updateMusic/:patientId", async (req, res) => {
    const patientId = req.params.patientId;
    const { music } = req.body;

    try {
        const result = await BreathingExercises.updateOne(
            { patient_id: patientId },
            { $set: { music: music } },
            
        );

        if (result.acknowledged && result.modifiedCount > 0) {
            res.json({ success: true, message: "" });
        } else {
            res.status(404).json({ error: "" });
        }
    } catch (error) {
        console.error("", error);
        res.status(500).json({ error: ` ${error.message}` });
    }
});

app.put("/updatePatientPassword", async (req, res) => {
    const { _id, password } = req.body;

    try {
        const existingPatient = await Patients.findOne({ _id: new ObjectId(_id) });
        if (password === existingPatient.password) {
            return res.status(422).json({ error: "New password matches the old password" });
        }

        const filter = { _id: new ObjectId(_id) };
        const update = {
            $set: {
                "password": password,
            },
        };

        const result = await Patients.updateOne(filter, update);

        if (result.modifiedCount > 0) {
            res.json({ success: true });
        } else {
            res.status(404).json({ error: "No matching Patient found" });
        }
    } catch (error) {
        console.error("Error updating password:", error);
        res.status(500).json({ error: "Failed to update password" });
    }
});

app.put("/updateTherapistPassword", async (req, res) => {
    const { _id, password } = req.body;
   
    try {
        const existingTherapist = await Therapists.findOne({ _id: new ObjectId(_id) });
        if (password === existingTherapist.password) {
            return res.status(422).json({ error: "New password matches the old password" });
        }

        const filter = { _id: new ObjectId(_id) };
        const update = {
            $set: {
                "password": password,
            },
        };

        const result = await Therapists.updateOne(filter, update);

        if (result.modifiedCount > 0) {
            res.json({ success: true });
        } else {
            res.status(404).json({ error: "No matching Therapist found" });
        }
    } catch (error) {
        console.error("Error updating password:", error);
        res.status(500).json({ error: "Failed to update password" });
    }
});

app.post('/profiledata/:therapistId', async (req, res) => {
    try {
        const therapistId = req.params.therapistId;
        const { newCharges, availableTime, selectedDegree } = req.body;
        
        
        
        const updateObject = {};
        if (newCharges !== null && newCharges !== undefined) {
            updateObject.$set = { charges: newCharges };
        }
        if (availableTime !== null && availableTime !== undefined) {
           
            updateObject.$set = { ...updateObject.$set, availableTime: availableTime };
        }
        if (selectedDegree !== null && selectedDegree !== undefined) {
           
            updateObject.$set = { ...updateObject.$set, degree: selectedDegree };
        }
        console.log('update object:', updateObject);
        
       
        const result = await Therapists.updateOne(
            { _id: new ObjectId(therapistId) }, 
            updateObject 
        );

        if (result.modifiedCount === 1) {
            res.status(200).json({ message: 'Therapist data updated successfully' });
        } else {
            res.status(404).json({ error: 'Therapist not found' });
        }
    } catch (error) {
        console.error('Error updating therapist data:', error);
        res.status(500).json({ error: 'Internal server error' });
    }
});


app.get('/gettherapistsdata/:therapistId', async (req, res) => {
    try {
        const therapistId = req.params.therapistId;
        const therapist = await Therapists.findOne({ _id: new ObjectId(therapistId) });

        if (!therapist) {
            return res.status(404).json({ error: 'Therapist not found' });
        }

      
        if (therapist.accountList) {
            therapist.accountList = therapist.accountList.map(accountData => {
                try {
                    const decryptedData = CryptoJS.AES.decrypt(accountData, key).toString(CryptoJS.enc.Utf8);
                    return JSON.parse(decryptedData);
                } catch (error) {
                    console.error('Error decrypting account data:', error);
                    return null; 
                }
            }).filter(account => account !== null); 
        }

        
        const fieldsToInclude = ['availableTime', 'charges', 'degree', 'Image', 'accountList'];

       
        fieldsToInclude.forEach(field => {
            if (!(field in therapist)) {
                therapist[field] = null;
            }
        });

       
        if (therapist.pdfFiles) {
            therapist.pdfFiles = await Promise.all(therapist.pdfFiles.map(async pdf => {
                try {
                    const pdfData = await fs.promises.readFile(pdf.pdfPath);
                    const base64PdfData = pdfData.toString('base64');
                    return {
                        originalPdfName: pdf.originalPdfName,
                        pdfData: base64PdfData
                    };
                } catch (error) {
                    console.error('Error reading PDF file:', error);
                    return null; 
                }
            })).then(files => files.filter(pdf => pdf !== null)); 
        }

       
        res.status(200).json(therapist);
        console.log('Therapist data:', therapist);
    } catch (error) {
        console.error('Error fetching therapist data:', error);
        res.status(500).json({ error: 'Internal server error' });
    }
});










app.post("/saveProfilePic", async (req, res) => {
    const { TherapistId, Image } = req.body;

    try {
    

        
        const existingTherapist = await Therapists.findOne({ _id: new ObjectId(TherapistId) });
        
        
        if (existingTherapist && existingTherapist.Image === Image) {
        
            res.status(200).send("Profile picture is already up to date");
            return;
        }

        const result = await Therapists.updateOne(
            { _id: new ObjectId(TherapistId) },
            { $set: { Image: Image } },
            { upsert: true }
        );

      
        res.status(200).send("Profile picture saved successfully");
    } catch (err) {
        console.error("Error saving profile picture:", err);
        res.status(500).send("Internal server error");
    }
});

app.post("/deleteProfilePic", async (req, res) => {
    const { TherapistId } = req.body;

    try {
    
        
        const existingTherapist = await Therapists.findOne({ _id: new ObjectId(TherapistId) });
        
        
        if (!existingTherapist) {
            res.status(404).send("Therapist not found");
            return;
        }

        
        if (!existingTherapist.Image) {
            res.status(400).send("Therapist does not have a profile picture");
            return;
        }

        const result = await Therapists.updateOne(
            { _id: new ObjectId(TherapistId) },
            { $unset: { Image: "" } } 
        );

       
        res.status(200).send("Profile picture deleted successfully");
    } catch (err) {
        console.error("Error deleting profile picture:", err);
        res.status(500).send("Internal server error");
    }
});


app.get('/therapists1', async (req, res) => {
    try {
        const therapists = await Therapists.find({
            availableTime: { $exists: true, $ne: [] },
            accountList:{ $exists: true, $ne: [] },
            charges: { $exists: true, $ne: "" },
            degree: { $exists: true, $ne: [] },
            pdfFiles: { $exists: true, $ne: "" },
            Image: { $exists: true, $ne: "" }
        }).toArray();

        res.json(therapists);
        console.log("Therapists retrieved:", therapists);
    } catch (err) {
        console.error('Error retrieving therapists:', err);
        res.status(500).json({ error: 'Internal server error' });
    }
});

function getDateTimeString(day, time) {
   
    const timeParts = time.match(/(\d+)(am|pm)/);
    let hours = parseInt(timeParts[1], 10);
    const period = timeParts[2];
    if (period === 'pm' && hours < 12) hours += 12;
    if (period === 'am' && hours === 12) hours = 0;

  
    const appointmentDateTime = new Date(`${day} ${hours}:00`);

   
    appointmentDateTime.setHours(appointmentDateTime.getHours() + 1);

    return appointmentDateTime;
}
app.get("/getAppointmentsOfChat", async (req, res) => {
    try {
        const now = new Date();
        const appointments = await Appointments.find({
            SessionType: 'Chat Session'
        }).toArray();

        const upcomingAppointments = appointments.filter(appointment => {
            const appointmentDateTime = getDateTimeString(appointment.day, appointment.time);
            return appointmentDateTime > now;
        });

        res.status(200).json(upcomingAppointments);
    } catch (error) {
        console.error("Error fetching appointments:", error);
        res.status(500).json({ message: "Internal server error" });
    }
});

app.get("/getAppointmentsOfVideo", async (req, res) => {
    try {
        const now = new Date();
        const appointments = await Appointments.find({
            SessionType: 'Video Session'
        }).toArray();

        const upcomingAppointments = appointments.filter(appointment => {
            const appointmentDateTime = getDateTimeString(appointment.day, appointment.time);
            return appointmentDateTime > now;
        });

        res.status(200).json(upcomingAppointments);
    } catch (error) {
        console.error("Error fetching appointments:", error);
        res.status(500).json({ message: "Internal server error" });
    }
});






const transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: 'anxietyrelief14@gmail.com',
        pass: 'nlsu ziee dijy jbua'
    }
});


async function sendCancellationEmail(patientEmail, therapistName, appointmentTime,appointmentday) {
    const mailOptions = {
        from: 'anxietyrelief14@gmail.com',
        to: patientEmail,
        subject: 'Appointment Cancellation',
        text: `Dear Patient,\n\nYour appointment with therapist ${therapistName} at ${appointmentday} ${appointmentTime} has been cancelled.\n\nRegards,\nAnxietyRelief`
    };

    try {
        await transporter.sendMail(mailOptions);
        console.log('Cancellation email sent successfully');
    } catch (error) {
        console.error('Error sending cancellation email:', error);
    }
}


app.delete("/appointments/:id", async (req, res) => {
    try {
        const appointmentId = req.params.id;
        const deletedAppointment = await Appointments.findOneAndDelete({ _id: new ObjectId(appointmentId) });

        if (!deletedAppointment) {
            return res.status(404).json({ message: "Appointment not found" });
        }
        console.log(deletedAppointment)

        
        const patient = await Patients.findOne({ _id: new ObjectId(deletedAppointment.patientId) });
        console.log(patient)
        if (!patient) {
            return res.status(404).json({ message: "Patient not found" });
        }

        const patientEmail = patient.email;
        const therapistName = deletedAppointment.therapistname        ;
        const appointmentTime = deletedAppointment.time;
     const appointmentday = deletedAppointment.day;
     const updatedTransaction = await Transactions.findOneAndUpdate(
        { _id: new ObjectId(deletedAppointment.transactionId) },
        { $set: { status: "rolledback" } },
        { returnOriginal: false } 
    );

    if (!updatedTransaction) {
        return res.status(404).json({ message: "Transaction not found" });
    }
       
       await sendCancellationEmail(patientEmail, therapistName, appointmentTime,appointmentday);

        res.status(200).json({ message: "Appointment deleted successfully" });
    } catch (error) {
        console.error("Error deleting appointment:", error);
        res.status(500).json({ message: "Internal server error" });
    }
});

app.put('/completeTransaction', async (req, res) => {
    const { transactionId } = req.body;

    if (!transactionId) {
        return res.status(400).json({ message: 'Transaction ID is required' });
    }

    try {
        const updatedTransaction = await Transactions.findOneAndUpdate(
            { _id: new ObjectId(transactionId) },
            { $set: { status: 'completed' } },
            { returnOriginal: false }
        );

        if (!updatedTransaction.value) {
            return res.status(404).json({ message: 'Transaction not found' });
        }

        res.status(200).json({
            message: `Transaction ${transactionId} status updated`,
           
        });
    } catch (error) {
        console.error('Error updating transaction status:', error);
        res.status(500).json({ message: 'Internal server error' });
    }
});




  


const PORT = process.env.PORT || 5000;
app.listen(PORT, () => {
    console.log('Server started on port ${PORT}');
});
