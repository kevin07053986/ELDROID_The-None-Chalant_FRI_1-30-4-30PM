// file path: server.js

const express = require('express');
const stripe = require('stripe')('sk_test_51PJJH3RvrHruUnqBuZLA5bf980sk2AYMf9DhAkfw8D2X2iWVSOkqd0cDiuNAfEnZtBks4ebdpowDg9b8trTjD8vC00Uboqwj2V'); // Replace with your Stripe secret key

const app = express();
const PORT = process.env.PORT || 4242;

app.use(express.json());

app.post('/create-payment-intent', async (req, res) => {
  const { amount } = req.body;

  try {
    const paymentIntent = await stripe.paymentIntents.create({
      amount,
      currency: 'usd',
    });

    res.send({
      clientSecret: paymentIntent.client_secret,
    });
  } catch (error) {
    res.status(500).send({ error: error.message });
  }
});

app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}`);
});