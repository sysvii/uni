% NWEN405 - Assignment 1
% David Barnett - 300313764

# Question 1 - Computer Security Concepts 

## a)

Privacy is a right of an individual to not have private information publicly
disclosed without consent.
An example of this is a patient has privacy rights to their medical records
so they may choose who can access them, such as trusted individuals like their GP.

Confidentially is the intention of keeping some information secret.
For example encrypting a message so only Bob can see Alice's message.

The key differences between these two concepts are:
 * that privacy is focused on the individual's right to keep information private, and
 * confidentially is focused on the intention of keeping the given information private.

## b)

A service that would have a moderate level of availability could be one
that provides a service that could be substituted easily if unavailable or
is not critical to the use of a greater service.
For example a single node in a cloud environment has a moderate availability
as they are generally built with commodity hardware and would be prone to fail.
This is would be fine for the greater service as there are enough nodes to
absorb the loss of availability of a single node over a large number of nodes.

## c)

The use of encryption can prevent some passive attacks on a user's data.
A release of message contents attack has the intent of reading a message
transmitted by some user. This involves reading the messages send by the
user, the use of encryption would prevent this information being read.
However, if they were to use traffic analysis on the encrypted messages
the user sent some information can still be scraped.
This is because the message will still retain some metadata about the message
itself unencrypted as it is required for the protocol or application to function.

## d)

The principle of least privilege is minimising the privilege of an
application or user to only what they require for the current task and request
more when required. If this is applied to the given context the user would
be prompted to elevate only the programs that require administration access,
as an operating system updater.
This would help to improve the security of their system so that any other programs
that are executed would not automatically be granted administration access.
This would of lessen the impact of the ransomware attack but would most likely
not prevent it.

## e)

An attack surface is the potential of an application or service to be
leveraged in the event on an attack, for example having anonymous access
to an FTP server or having the ECHO server open to the internet.

An attack tree is a formal method to describe the security of a system
by detailing the various methods that could attack a system with increasing
detail with the depth of the tree.

The difference between an attack surface and an attack tree is an
attack surface is the potential methods that could be utilised.
Where an attack tree uses the attack surfaces to describe the
security of the system.

## f)

![Attack Tree](./attack-tree.png)

# Question 2 - Malware

## a)

The three main propagation mechanisms for malware are: infection, exploitation and
social engineering.

To propagate via infection a malware would modify another program to include
the code for the virus itself.
To propagate itself the malware would then need to infect other applications
or devices, such as corrupting USB firmware or networked drives.
An example of this would be a malware that upon execution would embed itself
into the operating system and infect USB devices so it can then propagate to
more systems via the system user's USB devices.

Exploitation of bugs and misusing features is another method for malware to propagate.
The main idea behind this propagation technique is to exploit bugs in applications
and services to achieve a goal such as privilege escalation, arbitrary code execution, 
and so on. An example of this would be worms that would exploit weakness in network
protocols or applications to propagate themselves. An example would be a worm
is programmed to exploit a foreign code execution bug that would allow it to send
itself to the target machine and execute itself.

Social engineering is another method to propagate malware.
To use this propagation technique the attacker must have the intention to
trick the user to execute the malware, such as posing to be a useful program like
Photoshop. An example of this is providing cracks to popular applications or games
to trick users into installing the software, with presumably administration rights.
This can also be propagated via attachments for emails from "reputable" sources
or social media.

## b)

Four (plus a bonus) methods that a virus could conceal itself:

 1. Encrypts or compresses the main body of code
 2. Modifies its own code such that antivirus software cannot match a signature
    to it, e.g. padding itself with `NOOP` instructions
 3. Modifies the code of every infection so no two infections look the same,
    some may be found but maybe not all of them
 4. Disabling antivirus or other intrusion detection services
 5. Instructing the user to disable their antivirus via social engineering

## c)

### (i)

You should be suspicious of the game, as those permissions are
unrelated to the task you are installing it for.
This is because the game would have very little legitimate use
for these permissions than exploiting them or harvesting data.

### (ii)

Given the permissions of sending SMS messages and the address book
your phone has taken on the threat of being taken over to be used
a spam bot to send spam to contacts or paid SMS services.
This is because the mobile operating system has universal method
to prevent any abuse, some may prevent obvious abuse like
thousand of messages per minute, of these functions after the permission
is granted. In more modern versions of the mobile operating systems
a permission can be revoked after the fact which provides some mitigation
to these types of attacks.

### (iii)

The app would be a malware that has been propagated via social engineering.
This is due to desire to install the suspicious free version has been
created by the social desire to jump on the current trend and this malware
is exploiting that by providing a "free" version.

## d)

### (i)

An antivirus program would help to block a macro virus via email attachments.
This is because an antivirus program would be able to scan the contents of the
email attachments and have a database of malware signatures to be able to compare
the attachment to thus have a chance to stop the virus. However, a firewall
would be able to help since it can only filter the packets that are passed
by applications by lower layers of the network stack.
 
### (ii)

A firewall is the best tool to use to block a backdoor. This is because
with a firewall the user can configure it such that only known services are
permitted to send outward bound network traffic. By using a firewall 
the backdoor will be blocked from sending traffic back to its control server
thus making it useless.
An antivirus software cannot block the backdoor like a firewall,
but it could be able to detect malware operating the backdoor and remove it.
Since the use of a firewall can block the backdoor without knowledge of it,
the use of a firewall is better to block the use of backdoors over antivirus software.

## e)

Antivirus software use a range of heuristics scanners and activity traps to 
detect if some software is infected but not necessarily able to detect what it is.
This is because the heuristic scanners and activity traps monitor the system for
what they define as suspicious activity, such as an odd arrangement of system
calls, attempts to access kernel memory, etc.
These techniques are able to detect that some program is operating in an infected
manner and the antivirus can stop this. However, the observed behaviour
cannot be easily used to identify the virus as it may be proxying through different
exploits in many programs to hide the origin and that many viruses can use 
the same techniques and being able to differentiate between them is an undecidable problem
from behaviour alone.

## f)

### (i)

The method of propagation for an Advance Persistent Threat (APT)
is a targeted attack which has been specialised to appeal to the target.
This lends the attack to have a higher chance of success through personalised
or targeted social engineering attempts directed at the target.
This is called spear phishing as it requires a reasonable amount of
background knowledge of the target to pull off effectively and is
relatively successful.

The main method of Banking Trojans use to propagate via
malicious emails or by victims visiting websites infected with exploit kits.
The difference between this method and APT's is that the Banking Trojans
aim for anyone with a bank account to drain it where APT is a targeted
attack. The difference also lays in the intent in both attacks
where Banking Trojans wish to steal cash, an APT has a range of intents
from the attacker from political to finical.
The likely hood of a successful strike for a Banking Trojan relies more
on the defences of the victim, such as educated to not open poorly written
"bank" emails or having up-to-date operating system, browser and antivirus.
This differs from APT as Banking Trojans are about gaining success through
volume and APT is through getting a hole into the target.

### (ii)

The role of the APT is to open the door for the payload to
steal sensitive data.
This was achieved by exploiting a user opening the malicious
attached Microsoft\texttrademark{} Word document.
From there the payload of a command-control service
for the attacker other programs to collect data were deployed.

The role of the Banking Trojan's is to collect or trick
the victim into exposing their banking credentials.
This is achieved by installing a keylogger and additional
malware that would either proxy the login via the attackers
or by injecting additional fields into webpages to make the 
user reveal more credentials.

Both of the attacks have similarities and differences.
They both share the intent to deliver some payload 
to the target.
However, they different in what that payload set out to achieve.
The ADT payload sets out to establish control to the attacker
to retrieve sensitive data from the victim.
While Banking Trojans will autonomously attempt to
steal banking credentials from the victim.

### (iii)

The article does not talk about any countermeasures for the given APT attack.
From the described scenario some countermeasures, technical and non-technical,
can be derived.
Such as for a business to introduce a strong spam filter that has the capacity to detect,
either via signatures or executing in a sandboxing, malicious attachments to mitigate some
risk of dangerous attachments being sent to potential victims inside the target
organisation.
A non-technical solution is to educate or introduce policies so that the staff
know of unsolicited emails that could be exploited for an APT attack.
This could come in the form of having staff or email filters check an invoice
against an internal ledger of expected invoices or expected email.

The effectiveness of countermeasures for Banking Trojans are not discussed
in the article itself but in how the attacks are commenced some mitigation tactics
and countermeasures can be formed.
For example a technical solution to attack vector of exploit kits is to 
reduce the attack surface by ensuring the operating system, web browser and
plugins are up to date.
A non-technical to prevent these attacks would be to educate the general
public of their occurrences, such as malware weather reports and general
guidelines to follow for internet safety, e.g. do not open email from unknown
senders.

Both of these mitigation techniques are similar between APT and Banking Trojans.
However, APT is more focused on a targeted group than the general populace like
banking Trojans.


# Question 3 - Denial-of-Service (DoS)  attacks

## a)

The aim of for a denial-of-service attack on different resources
all have the aim of reducing or preventing the availability of the resource.
By targeting different resources they impact other resources.
For example by targeting the network bandwidth the other networked services
using that bandwidth is also denied, for example a bot net spamming a
node to saturate a link.
A denial-of-service attack on system resources, such as memory, disk or
CPU usage, not only the service under attack is impacted but all services
on the computer. An example of this would be using an exploit in the zip
format to decompress 4KiB to 4 terabytes in memory to deny other applications
memory space.
Another target resource is application resources, such as database connections 
or rate-limited API calls. A denial of service attack on this would disable the
application and other services that depend on it, such as an authentication
micro service that has exhausted its database connection pool from bogus requests.

## b)

The general principle behind a reflection attack is to
forge a request such that the response will be sent to
the target and not the attacker.
It is more advantageous for the attacker if the response is 
larger than the request allowing for a amplification of the attack
and can be requested anonymously.
An example of this would be forging a DNS request such that
the response IP address is set to the target rather than back
to the attacker.

## c)

A NTP reflection attack exploits the use of UDP packets
to ask for the current time from a server.
Since the NTP server accepts time queries from UDP packets
an attacker could specify a different machine to receive the
response of the query. The query is a magnitude larger than
the request thus the application of a denial-of-service attack
would be at least by a factor of 10.
Figure 2 shows the flow of packets reflecting from the attacker
to the target via the NTP server.
This kind of attack could be mitigated by using a TCP connection
instead of UDP as the TCP handshake will ensure the machine that
requests the data will be the recipient, thus preventing any reflection attack
but at the cost of latency as additional round trips are required.

![Diagram of NTP reflection attack](./ntp-reflection.png)

## d)

Backscatter traffic is generated by some types of denial of service attack
but not all.
Backscatter traffic is when a denial of service attack
will enlist innocent hosts to attack the target.
For example instead of TCP/SYN flooding a port on the target with randomly
forged source addresses, instead randomly send TCP/SYN requests to innocent
hosts with the forged address of the target machine.
This works by the random innocent hosts will all reply to the single target
host and with enough traffic will cause a denial of service.
This will also cover the attacker's trail as the physical packets are
not sent from their line but random innocent hosts.
Not all denial of service attacks can make use of backscatter attacks
as they might require a full TCP connection to request an overly busy task,
such as computing the million digit of $\pi$ to preform their denial 
of service.

## e)

### (i)

Maximum time to drop one entry from the table:

$5 \text{ total reties} * 30 \text{ seconds timeout till retry} = 150 \text{ seconds till dropped}$

Since it takes 150 seconds, or 2minutes and 30 seconds, for an entry in the table
to be dropped the attacker would only have to send a new TCP/SYN once every
150 seconds to maintain the attack, assuming no table flushes or early entry drops.

### (ii)

Total bits for a TCP/SYN

$40\text{ bytes} = 40 * 8 = 320\text{ bits}$

$\frac{320 \text{ bits}}{150 \text{ seconds}} = 2.1\dot{3} \text{ bits/second}$

An amortized cost of $2.1\dot{3}$ bits per second and a peak of $40$ bits per second
of bandwidth is required to maintain the attack.

### (iii)

Because the TCP connection pool is local to each port on the machine and not
all ports are accepting TCP packets but are accepting UDP packets so a connection
pool table is not required and thus cannot be filled.

## f)

### (i)

CloudFlare\texttrademark{} is considered a proxy instead of a CDN due
to how it sits in front of the service not instead of.
In the case of a CDN the service is replicated to multiple geographic
location around of the world to achieve locality.
CloudFlare\texttrademark{} is similar with its geographically distributed
data centers but they only hold a cache of the protected service instead
of a replica of it. In the case of a cache miss the request will also
be proxied via the data center the user has been directed to instead of
the user directly requesting the service, this allows CloudFlare\texttrademark{}
to cache the response and also make it act like a proxy.

### (ii)

The use of CloudFlare\texttrademark{} may present some additional security 
risks due to its caching and other features.

One such risk is that it is now one giant target for attacks that
could potentially hold sensitive information cached due to poor
configuration by origin sites using the service or due to bugs in
CloudFlare\texttrademark{} [^1].
This type of problem would not be solved with using HTTPS with
CloudFlare\texttrademark{}.
This is due to how CloudFlare\texttrademark{} is configured with
HTTPS, the documentation implies that the traffic from user to the
CloudFlare\texttrademark{} data centers are encrypted but internally are
not encrypted, just like in the man in the middle SSL attack [^2].
The contents of HTTPS requests are also cached so the contents of
these requests could also be leaked via an exploit.

[^1]: https://blog.cloudflare.com/incident-report-on-memory-leak-caused-by-cloudflare-parser-bug/
[^2]: https://www.cloudflare.com/ssl/

### (iii)

For a user to configure their domain to use CloudFlare\texttrademark{} they
would need to set their authoritative name servers for their domain to
be set to name servers under the control of CloudFlare\texttrademark{}.
After that setup whenever a DNS request for their domain is sent and
forwarded onto the CloudFlare\texttrademark{} name servers a feature of
DNS, Anycast, is used to then resolve the DNS to the best CloudFlare\texttrademark{}
data center to then hit cache or continue onto the main site.
Anycast is a network addressing and routing feature that allows
for routing based on some metric such as health of the destination, geographic
location and bandwidth to the lowest cost destination.
