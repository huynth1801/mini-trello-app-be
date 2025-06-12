<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Board Invitation</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            color: #333;
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
            background-color: #f4f4f4;
        }
        .container {
            background-color: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
        }
        .header {
            text-align: center;
            margin-bottom: 30px;
        }
        .header h1 {
            color: #2c3e50;
            margin-bottom: 10px;
        }
        .invitation-box {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin: 20px 0;
            border-left: 4px solid #007bff;
        }
        .board-name {
            font-size: 18px;
            font-weight: bold;
            color: #007bff;
            margin-bottom: 10px;
        }
        .owner-info {
            color: #666;
            margin-bottom: 15px;
        }
        .action-buttons {
            text-align: center;
            margin: 30px 0;
        }
        .btn {
            display: inline-block;
            padding: 12px 24px;
            margin: 0 10px;
            text-decoration: none;
            border-radius: 5px;
            font-weight: bold;
            transition: all 0.3s ease;
        }
        .btn-accept {
            background-color: #28a745;
            color: white;
        }
        .btn-accept:hover {
            background-color: #218838;
        }
        .btn-decline {
            background-color: #dc3545;
            color: white;
        }
        .btn-decline:hover {
            background-color: #c82333;
        }
        .footer {
            text-align: center;
            margin-top: 30px;
            padding-top: 20px;
            border-top: 1px solid #eee;
            color: #666;
            font-size: 14px;
        }
        .logo {
            width: 60px;
            height: 60px;
            margin: 0 auto 20px;
            background-color: #007bff;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 24px;
            font-weight: bold;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <div class="logo">📋</div>
        <h1>You're Invited!</h1>
        <p>Someone wants to collaborate with you on a board</p>
    </div>

    <div class="invitation-box">
        <div class="board-name">📋 ${boardName}</div>
        <div class="owner-info">
            <strong>${ownerName}</strong> has invited you to join their board and start collaborating together.
        </div>
        <p>Join this board to view and manage tasks, share ideas, and work together more effectively.</p>
    </div>

    <div class="action-buttons">
        <a href="${invitationLink}?action=accept" class="btn btn-accept">
            Accept Invitation
        </a>
        <a href="${invitationLink}?action=decline" class="btn btn-decline">
            Decline Invitation
        </a>
    </div>

    <div class="footer">
        <p>This invitation was sent to you because someone wanted to share their board with you.</p>
        <p>If you didn't expect this invitation, you can safely ignore this email.</p>
        <p><strong>Invitation ID:</strong> ${inviteId}</p>
    </div>
</div>
</body>
</html>