const https = require('https');
const url = 'https://xiyhfrxnjwzldkssqgse.supabase.co/rest/v1/?apikey=' + process.env.SUPABASE_KEY;

https.get(url, (res) => {
    let data = '';
    res.on('data', chunk => data += chunk);
    res.on('end', () => {
        try {
            const swagger = JSON.parse(data);
            const paths = Object.keys(swagger.paths);
            console.log("Available paths in Supabase:");
            paths.forEach(p => console.log(p));
        } catch(e) {
            console.log("Error parsing JSON:", e.message);
            console.log(data.substring(0, 500));
        }
    });
}).on('error', err => console.log(err));
