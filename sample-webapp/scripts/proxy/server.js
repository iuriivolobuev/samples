const express = require('express');
const {createProxyMiddleware} = require('http-proxy-middleware');
const path = require('path');

const app = express();

app.use('/static', express.static(
    path.join(__dirname, '..', '..', 'src', 'main', 'resources', 'static')
));

app.use('/', createProxyMiddleware({
    target: 'http://localhost:8080/',
    ws: true
}));

app.listen(3000, () => {
    console.log('Proxy server is running on port 3000');
});
