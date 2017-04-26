# attTrack-WIP

attTrack is a Work in progress application that automatically logs attendance details.

The application uses a combination of GPS location proximity sensing using Google Distance Matrix API and QR codes (from Zxing library)
to authenticate the user for checking into a classroom of concern.

The database gets updated with the current attendance details, and has a cronJob that will reset attendance everyday so that the table 
may be used to record for the next day.

The user has to be within 500m of the location they are supposed to be in order to checkIn and scan the QR code. The QR code is a uniquely 
random 10 character string which contains alphanumeric characters. It can be changed as per the wish of the faculty and updated so that 
users have to scan it before validating their attendance.

The faculty can also generate a report of the attendance details for the day by logging into the app.
