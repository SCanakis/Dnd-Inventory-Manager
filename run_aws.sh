#!/bin/bash
# Run this once on your EC2 instance

echo "🚀 Setting up EC2 instance for DndApp deployment..."

# Update system
sudo apt update && sudo apt upgrade -y

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Clone your app
git clone https://github.com/SCanakis/DndApp.git ~/app
cd ~/app
chmod +x start.sh

echo "Done! Log out and back in, then run: cd ~/app && ./start.sh --with-all"