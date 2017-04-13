using System;

namespace CalWatch.Api.Exceptions
{
    /// <summary>
    /// Requested object was not found (e.g. user with id 123 does not exist)
    /// </summary>
    [Serializable]
    public class EntityNotFoundException : Exception
    {
        public EntityNotFoundException() : base() { }
        public EntityNotFoundException(string message) : base(message) { }
    }
}
