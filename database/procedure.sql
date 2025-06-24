CREATE PROCEDURE [InsertSampleSongGenres] 
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Biến để theo dõi tiến trình
    DECLARE @songCount INT;
    DECLARE @genreCount INT;
    DECLARE @insertedCount INT = 0;
    
    -- Lấy số lượng bài hát và thể loại
    SELECT @songCount = COUNT(*) FROM songs;
    SELECT @genreCount = COUNT(*) FROM genres;
    
    -- Kiểm tra điều kiện để thực hiện
    IF @songCount = 0
    BEGIN
        PRINT N'Không có bài hát nào trong cơ sở dữ liệu để gán thể loại';
        RETURN;
    END
    
    IF @genreCount = 0
    BEGIN
        PRINT N'Không có thể loại nào trong cơ sở dữ liệu để gán cho bài hát';
        RETURN;
    END
    
    BEGIN TRY
        -- Xóa dữ liệu cũ trong bảng song_genres
        DELETE FROM song_genres;
        PRINT N'Đã xóa dữ liệu cũ trong bảng song_genres';
        
        -- Tạo bảng tạm để lưu các bài hát và thể loại muốn gán
        DECLARE @songGenreMappings TABLE (
            song_id INT,
            genre_id1 INT,
            genre_id2 INT
        );
        
        -- Thêm các ánh xạ bài hát-thể loại mong muốn
        INSERT INTO @songGenreMappings (song_id, genre_id1, genre_id2) VALUES
        (1, 1, 5),    -- Bài hát 1: Pop, Electronic
        (2, 2, 19),   -- Bài hát 2: Rock, Alternative
        (3, 3, 4),    -- Bài hát 3: Hip Hop, R&B
        (4, 6, 13),   -- Bài hát 4: Jazz, Blues
        (5, 17, 1);   -- Bài hát 5: EDM, Pop
        
        -- Thêm các bản ghi cho mỗi cặp bài hát-thể loại
        -- Quy trình này kiểm tra cả bài hát và thể loại tồn tại trước khi chèn
        DECLARE @currentSongId INT;
        DECLARE @currentGenreId1 INT;
        DECLARE @currentGenreId2 INT;
          -- Xử lý tất cả các bài hát trong danh sách ánh xạ cố định (tối đa 5 bài hát)
        DECLARE @processCount INT = CASE 
                                     WHEN @songCount < 5 THEN @songCount
                                     ELSE 5
                                   END;
        
        -- Lấy danh sách ID bài hát thực tế
        DECLARE @availableSongs TABLE (
            id INT,
            row_num INT IDENTITY(1,1)
        );
        
        INSERT INTO @availableSongs (id)
        SELECT TOP (@processCount) id FROM songs ORDER BY id;
        
        -- Xử lý từng bản ghi trong songGenreMappings
        DECLARE @i INT = 1;
        WHILE @i <= @processCount
        BEGIN
            -- Lấy ID bài hát thực tế dựa trên vị trí
            DECLARE @actualSongId INT;
            SELECT @actualSongId = id FROM @availableSongs WHERE row_num = @i;
            
            -- Lấy thông tin mapping
            SELECT @currentGenreId1 = genre_id1,
                   @currentGenreId2 = genre_id2
            FROM @songGenreMappings 
            WHERE song_id = @i;
            
            -- Kiểm tra và chèn thể loại đầu tiên
            IF EXISTS (SELECT 1 FROM genres WHERE id = @currentGenreId1)
            BEGIN
                INSERT INTO song_genres (song_id, genre_id)
                VALUES (@actualSongId, @currentGenreId1);
                
                SET @insertedCount = @insertedCount + 1;
                PRINT N'Đã thêm thể loại ' + CAST(@currentGenreId1 AS NVARCHAR(10)) + 
                      N' cho bài hát có ID ' + CAST(@actualSongId AS NVARCHAR(10));
            END
            
            -- Kiểm tra và chèn thể loại thứ hai
            IF EXISTS (SELECT 1 FROM genres WHERE id = @currentGenreId2)
            BEGIN
                INSERT INTO song_genres (song_id, genre_id)
                VALUES (@actualSongId, @currentGenreId2);
                
                SET @insertedCount = @insertedCount + 1;
                PRINT N'Đã thêm thể loại ' + CAST(@currentGenreId2 AS NVARCHAR(10)) + 
                      N' cho bài hát có ID ' + CAST(@actualSongId AS NVARCHAR(10));
            END
            
            SET @i = @i + 1;
        END
          -- Xử lý tất cả các bài hát còn lại bằng cách gán ngẫu nhiên
        IF @songCount > @processCount
        BEGIN
            -- Lấy danh sách tất cả các bài hát còn lại
            DECLARE @remainingSongs TABLE (
                id INT,
                row_num INT IDENTITY(1,1)
            );
            
            INSERT INTO @remainingSongs (id)
            SELECT id FROM songs 
            WHERE id NOT IN (SELECT id FROM @availableSongs)
            ORDER BY id;
            
            DECLARE @remainingCount INT = @songCount - @processCount;
            DECLARE @j INT = 1;
            
            -- Xử lý tất cả các bài hát còn lại
            WHILE @j <= @remainingCount
            BEGIN                DECLARE @randomSongId INT;
                DECLARE @randomGenreId1 INT;
                DECLARE @randomGenreId2 INT;
                
                SELECT @randomSongId = id FROM @remainingSongs WHERE row_num = @j;
                
                -- Chọn hai thể loại ngẫu nhiên khác nhau cho mỗi bài hát
                SET @randomGenreId1 = CAST((RAND() * @genreCount) + 1 AS INT);
                SET @randomGenreId2 = CAST((RAND() * @genreCount) + 1 AS INT);
                
                -- Đảm bảo 2 thể loại khác nhau
                WHILE @randomGenreId1 = @randomGenreId2 AND @genreCount > 1
                BEGIN
                    SET @randomGenreId2 = CAST((RAND() * @genreCount) + 1 AS INT);
                END
                
                -- Thêm thể loại đầu tiên
                IF EXISTS (SELECT 1 FROM genres WHERE id = @randomGenreId1)
                BEGIN
                    -- Kiểm tra xem quan hệ đã tồn tại chưa
                    IF NOT EXISTS (SELECT 1 FROM song_genres 
                                   WHERE song_id = @randomSongId AND genre_id = @randomGenreId1)
                    BEGIN
                        INSERT INTO song_genres (song_id, genre_id)
                        VALUES (@randomSongId, @randomGenreId1);
                        
                        SET @insertedCount = @insertedCount + 1;
                        PRINT N'Đã thêm thể loại ngẫu nhiên ' + CAST(@randomGenreId1 AS NVARCHAR(10)) + 
                              N' cho bài hát có ID ' + CAST(@randomSongId AS NVARCHAR(10));
                    END
                END
                
                -- Thêm thể loại thứ hai nếu khác thể loại thứ nhất
                IF @randomGenreId1 <> @randomGenreId2 AND EXISTS (SELECT 1 FROM genres WHERE id = @randomGenreId2)
                BEGIN
                    -- Kiểm tra xem quan hệ đã tồn tại chưa
                    IF NOT EXISTS (SELECT 1 FROM song_genres 
                                   WHERE song_id = @randomSongId AND genre_id = @randomGenreId2)
                    BEGIN
                        INSERT INTO song_genres (song_id, genre_id)
                        VALUES (@randomSongId, @randomGenreId2);
                        
                        SET @insertedCount = @insertedCount + 1;
                        PRINT N'Đã thêm thể loại ngẫu nhiên ' + CAST(@randomGenreId2 AS NVARCHAR(10)) + 
                              N' cho bài hát có ID ' + CAST(@randomSongId AS NVARCHAR(10));
                    END
                END
                
                SET @j = @j + 1;
            END
        END
        
        PRINT N'Đã chèn thành công ' + CAST(@insertedCount AS NVARCHAR(10)) + N' quan hệ thể loại cho bài hát';
    END TRY
    BEGIN CATCH
        PRINT N'Lỗi khi chèn dữ liệu thể loại: ' + ERROR_MESSAGE();
    END CATCH
    
    SET NOCOUNT OFF;
END
GO
