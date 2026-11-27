#!/usr/bin/env node
'use strict';
/*
 * Manifiesto firmado de una release de Nova Client (lo comprueba el launcher Nova PX, lib/nova.js).
 *  dist/nova-client.json       { v: 1, version, files: { "<archivo>": "<sha256>" } }
 *  dist/nova-client.firma.json { v: 1, alg: "ed25519", nombre: "nova-client", firmadoEn, sha256, firma }
 * La firma cubre "NPX-DATA1\nnova-client\n<firmadoEn>\n<sha256 del manifiesto>", igual que lib/firma.js del launcher.
 * La llave privada llega solo como secreto de GitHub Actions (NOVA_FIRMA_PRIVADA); nunca va al repositorio.
 * Uso: NOVA_FIRMA_PRIVADA="$SECRETO" node tools/firmar-release.js <versión> <archivo>...
 */
const fs = require('fs');
const path = require('path');
const crypto = require('crypto');

const [version, ...files] = process.argv.slice(2);
const priv = process.env.NOVA_FIRMA_PRIVADA || '';
if (!version || !files.length) { console.error('Uso: firmar-release.js <versión> <archivo>...'); process.exit(1); }
if (!priv.trim()) { console.error('Falta NOVA_FIRMA_PRIVADA.'); process.exit(1); }
const key = crypto.createPrivateKey(priv);
if (key.asymmetricKeyType !== 'ed25519') { console.error('La llave no es Ed25519.'); process.exit(1); }

const sha256 = (b) => crypto.createHash('sha256').update(b).digest('hex');
const out = { v: 1, version: String(version).replace(/^v/, ''), files: {} };
for (const f of files) out.files[path.basename(f)] = sha256(fs.readFileSync(f));
const bytes = Buffer.from(JSON.stringify(out, null, 1) + '\n', 'utf8');
const firmadoEn = new Date().toISOString();
const hash = sha256(bytes);
const msg = Buffer.from(`NPX-DATA1\nnova-client\n${firmadoEn}\n${hash}`, 'utf8');
const sig = { v: 1, alg: 'ed25519', nombre: 'nova-client', firmadoEn, sha256: hash, firma: crypto.sign(null, msg, key).toString('base64') };
fs.mkdirSync('dist', { recursive: true });
fs.writeFileSync('dist/nova-client.json', bytes);
fs.writeFileSync('dist/nova-client.firma.json', JSON.stringify(sig, null, 1) + '\n');
console.log(`Manifiesto firmado: ${Object.keys(out.files).length} archivos.`);
