const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');
const cors = require('cors');

const app = express();
app.use(cors());

app.use('/', createProxyMiddleware({
  target: 'http://localhost:8080/',
  changeOrigin: true
}));

app.listen(3000, () => {
  console.log('Proxy server is running on port 3000');
});
