using System;
using System.Security.Cryptography;

namespace CalWatch.Api.Utilities
{
    public static class PasswordUtil
    {
        /// <summary>
        /// Hashes password combined with a given random salt
        /// </summary>
        /// <param name="password"></param>
        /// <param name="salt"></param>
        /// <returns></returns>
        public static string SaltAndHashPassword(string password, string salt)
        {
            byte[] passwordBytes = System.Text.ASCIIEncoding.ASCII.GetBytes(password);
            byte[] saltBytes = System.Text.ASCIIEncoding.ASCII.GetBytes(salt);

            HashAlgorithm algorithm = new SHA256Managed();

            byte[] plainTextWithSaltBytes =
              new byte[passwordBytes.Length + saltBytes.Length];

            for (int i = 0; i < passwordBytes.Length; i++)
            {
                plainTextWithSaltBytes[i] = passwordBytes[i];
            }
            for (int i = 0; i < salt.Length; i++)
            {
                plainTextWithSaltBytes[passwordBytes.Length + i] = saltBytes[i];
            }

            return Convert.ToBase64String(algorithm.ComputeHash(plainTextWithSaltBytes)); 
        }

        /// <summary>
        /// Compares a given plaintext password to its hashed version, to determine 
        /// a match. 
        /// </summary>
        /// <param name="password">Plaintext password to compare.</param>
        /// <param name="hash">A hashed/salted version of some password.</param>
        /// <param name="salt">The salt used to hash it.</param>
        /// <returns></returns>
        public static bool ComparePassword(string password, string hash, string salt)
        {
            string saltedAndHashedPassword = SaltAndHashPassword(password, salt);
            return (saltedAndHashedPassword == hash); 
        }

        /// <summary>
        /// Generates a random string to be used as a salt.
        /// </summary>
        /// <returns></returns>
        public static string GenerateNewSalt()
        {
            return Guid.NewGuid().ToString(); 
        }
    }
}
