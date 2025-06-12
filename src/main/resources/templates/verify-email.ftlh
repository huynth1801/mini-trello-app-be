<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta http-equiv="x-ua-compatible" content="ie=edge" />
    <title>Verify Email</title>
    <meta name="viewport" content="width=device-width, initial-scale=1" />
</head>
<body style="margin: 0; padding: 0; background-color: cornsilk; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;">
<table role="presentation" border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr>
        <td align="center" style="background-color: cornsilk; padding: 24px;">
            <table role="presentation" border="0" cellpadding="0" cellspacing="0" width="100%" style="max-width: 600px; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 6px rgba(0,0,0,0.1);">
                <tr>
                    <td align="center" style="padding: 36px 24px 12px; border-top: 4px solid #1a82e2;">
                        <h1 style="margin: 0; font-size: 28px; font-weight: bold; color: #333;">Verify Your Email</h1>
                    </td>
                </tr>
                <tr>
                    <td align="left" style="padding: 0 24px 24px;">
                        <p style="font-size: 16px; line-height: 24px; color: #555;">
                            Please enter the verification code below to complete your registration.
                        </p>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="padding: 12px 24px 36px;">
                        <div style="display: inline-block; padding: 16px 36px; background-color: #1a82e2; color: #ffffff; font-size: 24px; font-weight: bold; border-radius: 6px; letter-spacing: 2px;">
                            ${token}
                        </div>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="padding: 12px 24px; font-size: 12px; color: #999;">
                        If you did not request this email, you can safely ignore it.
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>
</body>
</html>
