install:
	@if [ -f /etc/debian_version ]; then \
		echo "Detected Debian/Ubuntu system. Installing dependencies..."; \
		sudo apt install -y openjdk-21-jdk maven && \
		mvn clean install && \
		curl -L https://github.com/watchexec/watchexec/releases/download/v2.3.2/watchexec-2.3.2-x86_64-unknown-linux-gnu.tar.xz -o /tmp/watchexec.tar.xz && \
		cd /tmp && \
		tar -xf watchexec.tar.xz && \
		sudo mv watchexec-2.3.2-x86_64-unknown-linux-gnu/watchexec /usr/local/bin/ && \
		sudo chmod +x /usr/local/bin/watchexec; \
	else \
		echo "Warning: This install script is intended for Debian/Ubuntu only. Skipping install."; \
	fi

run-dev:
	watchexec -r -e java "mvn compile exec:java -Dexec.args='-dev'"

run:
	mvn compile exec:java
