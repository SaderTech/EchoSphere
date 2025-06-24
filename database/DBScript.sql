-- Kiểm tra nếu cơ sở dữ liệu đã tồn tại thì xóa đi để tạo mới
IF DB_ID('EchoSphere') IS NOT NULL
BEGIN
    ALTER DATABASE EchoSphere SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE EchoSphere;
END
GO

-- Tạo cơ sở dữ liệu mới
CREATE DATABASE EchoSphere;
GO

-- Sử dụng cơ sở dữ liệu vừa tạo
USE EchoSphere;
GO

-- =============================================
-- Bảng người dùng và vai trò
-- =============================================

-- Bảng lưu thông tin người dùng
CREATE TABLE [users] (
    [id] INT NOT NULL IDENTITY(1,1),
    [username] VARCHAR(50) NOT NULL,
    [first_name] NVARCHAR(255),
    [last_name] NVARCHAR(255),
    [email] VARCHAR(255) NOT NULL,
    [password_hash] VARCHAR(255) NOT NULL,
    [registration_date] DATETIME DEFAULT GETDATE(),
    [avatar_url] VARCHAR(255),

    -- Khai báo khóa chính và ràng buộc duy nhất
    CONSTRAINT [PK_users] PRIMARY KEY ([id]),
    CONSTRAINT [UQ_users_username] UNIQUE ([username]),
    CONSTRAINT [UQ_users_email] UNIQUE ([email])
);
GO

-- Bảng lưu các vai trò (ví dụ: admin, user, artist)
CREATE TABLE [roles] (
    [id] INT NOT NULL IDENTITY(1,1),
    [role_name] VARCHAR(50) NOT NULL,

    CONSTRAINT [PK_roles] PRIMARY KEY ([id]),
    CONSTRAINT [UQ_roles_role_name] UNIQUE ([role_name])
);
GO

-- Bảng nối nhiều-nhiều giữa người dùng và vai trò
CREATE TABLE [user_roles] (
    [user_id] INT NOT NULL,
    [role_id] INT NOT NULL,

    -- Khóa chính phức hợp để đảm bảo một user chỉ có một vai trò nhất định một lần
    CONSTRAINT [PK_user_roles] PRIMARY KEY ([user_id], [role_id]),

    -- Khóa ngoại tham chiếu đến bảng users và roles
    CONSTRAINT [FK_user_roles_users] FOREIGN KEY ([user_id]) REFERENCES [users]([id]) ON DELETE CASCADE,
    CONSTRAINT [FK_user_roles_roles] FOREIGN KEY ([role_id]) REFERENCES [roles]([id]) ON DELETE CASCADE
);
GO

-- =============================================
-- Bảng nghệ sĩ, album, và thể loại
-- =============================================

-- Bảng lưu thông tin nghệ sĩ
-- Một nghệ sĩ có thể là một người dùng trên hệ thống
CREATE TABLE [artists] (
    [id] INT NOT NULL IDENTITY(1,1),
    [name] NVARCHAR(255) NOT NULL,
    [profile_image_url] VARCHAR(255),
    [user_id] INT, -- Có thể là NULL nếu nghệ sĩ không phải là người dùng

    CONSTRAINT [PK_artists] PRIMARY KEY ([id]),
    -- Một tài khoản người dùng chỉ có thể liên kết với một hồ sơ nghệ sĩ
    CONSTRAINT [FK_artists_users] FOREIGN KEY ([user_id]) REFERENCES [users]([id]) ON DELETE SET NULL
);
GO

-- Bảng lưu thông tin album
CREATE TABLE [albums] (
    [id] INT NOT NULL IDENTITY(1,1),
    [title] NVARCHAR(255) NOT NULL, -- Đổi tên từ 'name_album'
    [artist_id] INT NOT NULL, -- Một album thuộc về một nghệ sĩ chính
    [release_year] INT,
    [cover_art_url] VARCHAR(255),

    CONSTRAINT [PK_albums] PRIMARY KEY ([id]),
    CONSTRAINT [FK_albums_artists] FOREIGN KEY ([artist_id]) REFERENCES [artists]([id]) ON DELETE CASCADE
);
GO

-- Bảng lưu các thể loại nhạc
CREATE TABLE [genres] (
    [id] INT NOT NULL IDENTITY(1,1),
    [name] NVARCHAR(50) NOT NULL,

    CONSTRAINT [PK_genres] PRIMARY KEY ([id]),
    CONSTRAINT [UQ_genres_name] UNIQUE ([name])
);
GO

-- =============================================
-- Bảng bài hát và các quan hệ
-- =============================================

-- Bảng lưu thông tin bài hát
CREATE TABLE [songs] (
    [id] INT NOT NULL IDENTITY(1,1),
    [title] NVARCHAR(255) NOT NULL,
    [album_id] INT, -- Một bài hát có thể không thuộc album nào (single)
    [song_url] VARCHAR(255) NOT NULL,
    [duration_seconds] INT,

    CONSTRAINT [PK_songs] PRIMARY KEY ([id]),
    CONSTRAINT [FK_songs_albums] FOREIGN KEY ([album_id]) REFERENCES [albums]([id]) ON DELETE SET NULL
);
GO

-- Bảng nối nhiều-nhiều giữa bài hát và nghệ sĩ (hỗ trợ nhiều nghệ sĩ cho một bài hát)
CREATE TABLE [song_artists] (
    [song_id] INT NOT NULL,
    [artist_id] INT NOT NULL,

    CONSTRAINT [PK_song_artists] PRIMARY KEY ([song_id], [artist_id]),
    CONSTRAINT [FK_song_artists_songs] FOREIGN KEY ([song_id]) REFERENCES [songs]([id]) ON DELETE CASCADE,
    CONSTRAINT [FK_song_artists_artists] FOREIGN KEY ([artist_id]) REFERENCES [artists]([id]) ON DELETE CASCADE
);
GO

-- Bảng nối nhiều-nhiều giữa bài hát và thể loại
CREATE TABLE [song_genres] (
    [song_id] INT NOT NULL,
    [genre_id] INT NOT NULL,

    CONSTRAINT [PK_song_genres] PRIMARY KEY ([song_id], [genre_id]),
    CONSTRAINT [FK_song_genres_songs] FOREIGN KEY ([song_id]) REFERENCES [songs]([id]) ON DELETE CASCADE,
    CONSTRAINT [FK_song_genres_genres] FOREIGN KEY ([genre_id]) REFERENCES [genres]([id]) ON DELETE CASCADE
);
GO

-- =============================================
-- Bảng Playlist
-- =============================================

-- Bảng lưu thông tin playlist
CREATE TABLE [playlists] (
    [id] INT NOT NULL IDENTITY(1,1),
    [name] NVARCHAR(255) NOT NULL,
    [user_id] INT NOT NULL, -- Một người dùng có thể tạo nhiều playlist
    [is_public] BIT DEFAULT 1,
    [created_date] DATETIME DEFAULT GETDATE(),

    CONSTRAINT [PK_playlists] PRIMARY KEY ([id]),
    CONSTRAINT [FK_playlists_users] FOREIGN KEY ([user_id]) REFERENCES [users]([id]) ON DELETE CASCADE
);
GO

-- Bảng nối nhiều-nhiều giữa playlist và bài hát
CREATE TABLE [playlist_songs] (
    [playlist_id] INT NOT NULL,
    [song_id] INT NOT NULL,
    [date_added] DATETIME DEFAULT GETDATE(),

    CONSTRAINT [PK_playlist_songs] PRIMARY KEY ([playlist_id], [song_id]),
    CONSTRAINT [FK_playlist_songs_playlists] FOREIGN KEY ([playlist_id]) REFERENCES [playlists]([id]) ON DELETE CASCADE,
    CONSTRAINT [FK_playlist_songs_songs] FOREIGN KEY ([song_id]) REFERENCES [songs]([id]) ON DELETE CASCADE
);
GO
