#!/bin/bash

# Script para generar un JWT secret seguro (256 bits / 32 bytes)
# Ejecuta este script para generar un nuevo JWT_SECRET para producción

echo "🔐 Generando JWT_SECRET seguro (256 bits)..."
echo ""

# Generar 32 bytes aleatorios y codificar en base64
JWT_SECRET=$(openssl rand -base64 32)

echo "✅ JWT_SECRET generado:"
echo ""
echo "$JWT_SECRET"
echo ""
echo "📋 Copia este valor y configúralo como variable de entorno en Render:"
echo "   JWT_SECRET=$JWT_SECRET"
echo ""
echo "⚠️  IMPORTANTE: Mantén este secret seguro y nunca lo subas a Git!"